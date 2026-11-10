package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.*;
import br.com.keys.keys_api.model.dto.*;
import br.com.keys.keys_api.model.entity.EmpresaEntity;
import br.com.keys.keys_api.model.entity.ReservaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.EmpresaRepository;
import br.com.keys.keys_api.repository.ReservaRepository;
import br.com.keys.keys_api.repository.UsuarioRepository;
//import br.com.keys.keys_api.security.JwtService;
//import br.com.keys.keys_api.security.UserDetailsImpl;
import br.com.keys.keys_api.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; //verificar se a senha hash é igual a outra senha. Compara duas hash
    private final EmpresaRepository empresaRepository;
    private final ReservaRepository reservaRepository;

    public List<FindAllUSersResponse> buscarTodosUsuarios(String token) {

        //Verifica se é adm o dono do token (preciso separar o bearer do token pra evitar erros:
        String email_user = this.extrairToken(token);
        UsuarioEntity adm = this.usuarioRepository.findByNmEmailUsuarioAndChefeIsNull(email_user).orElseThrow(()-> new UsuarioInexistenteException("Admin inexistente!"));

        //Busca users dessa empresa:
        List<UsuarioEntity> usuarios = this.usuarioRepository.findByEmpresa(adm.getEmpresa());
        if(usuarios.isEmpty()) {
            throw new UsuarioInexistenteException("Nenhum user criado por esse adm!");
        } else {
            return usuarios.stream().map(this::mapearUsuarios).toList();
        }
    }

    public FindUserByIdResponse buscarUsuarioPorId(String id) {
        if(id.trim().isEmpty()) {
         throw  new UsuarioInexistenteException("Id inválido, ou seja, usuário inexistente!");
        } else {
            UsuarioEntity user = this.usuarioRepository.findById(id).orElseThrow(()-> new UsuarioInexistenteException("Usuário não encontrado!"));
            return new FindUserByIdResponse(user.getNmUsuario(),user.getNmEmailUsuario(), user.getNmLinkImagemUsuario());
        }
    }

    public FindAllUSersResponse mapearUsuarios(UsuarioEntity u) {
        return new FindAllUSersResponse(u.getCdUsuario(), u.getNmUsuario(), u.getNmEmailUsuario(), u.getNmLinkImagemUsuario()); //pode gerar exceção
    }



   public void deletarUsuario(String id, String token) {
        if(id.trim().isEmpty()) {
            throw  new UsuarioInexistenteException("Id inválido, ou seja, usuário inexistente!");
        } else {
            //Buscando pra ver se quem está solicitando a exclusão é o adm:
            String email_user = this.extrairToken(token);
            UsuarioEntity chefeToken = this.usuarioRepository.findByNmEmailUsuarioAndChefeIsNull(email_user).orElseThrow(()-> new UsuarioInexistenteException("Admin do token é inexistente!!!! Só admins excluem usuários!"));

            //verificando se este user existe (a ser excluído):
           UsuarioEntity user = this.usuarioRepository.findById(id).orElseThrow(() -> new UsuarioInexistenteException("Não é possível excluir um usuário inexistente! Envie um id válido!"));

            //Verificando se foi este adm que crio esse betinha
            //Antes, vendo se quem vai ser excluído é admin:
            if(user.getChefe() == null) {
                throw new PermissaoNegadaException("Admins não podem ser excluídos por aqui!");
            }
            UsuarioEntity adminLarpResenha = user.getChefe();
            if(!adminLarpResenha.getCdUsuario().equals(chefeToken.getCdUsuario())) {
                throw new AdminOutraEmpresaException("Este admin é de outra empresa. Portanto, não há permissão para excluir este user!");
            }

            //Excluindo reservas antes e user depois (se só excluir user, dar b.o):
            //ver se há reservas antes, cabaço!
            List<ReservaEntity> reservasUser = this.reservaRepository.findByProprietario(user);
            if(!reservasUser.isEmpty()) {
                this.reservaRepository.deleteByProprietario(user);
            }
            this.usuarioRepository.deleteById(id);
        }
    }


    /*
    public UpdateSenhaByEmailResponse updateSenhaByEmail(UpdateSenhaByEmailRequest updateSenhaByEmailRequest) {
        if (updateSenhaByEmailRequest.nm_senha_nova().trim().equals(updateSenhaByEmailRequest.nm_senha_nova_confirmada().trim())) {

        }
    }
    */


    public UpdateUserResponse atualizarInformacoesUsuario(String id, UpdateUserRequest updateUserRequest, String token) {
        //Verificar se o user do id existe ou não:
        UsuarioEntity usuario = this.usuarioRepository.findById(id).orElseThrow(()-> new UsuarioInexistenteException("Usuário não encontrado!"));

        //Verificando (se for adm da mesma empresa que ele ou se for o próprio user):

        UsuarioEntity user = this.usuarioRepository.findById(id).orElseThrow(() -> new UsuarioInexistenteException("Usuário inexistente a ser alterado!"));

        String email_user = this.extrairToken(token);
        UsuarioEntity userToken = this.usuarioRepository.findOptionalBynmEmailUsuario(email_user).orElseThrow( ()-> new UsuarioInexistenteException("Usuário ou admin do token não encontrados!"));

        if(userToken.getCdUsuario().equals(user.getCdUsuario()) || (userToken.getChefe() == null && userToken.getEmpresa() == user.getEmpresa())) {
            //Verificando os dados recebidos e inserindo se existirem:
            if(updateUserRequest.nm_usuario() != null && !updateUserRequest.nm_usuario().trim().isEmpty()) {
                usuario.setNmUsuario(updateUserRequest.nm_usuario());
            }
            if(updateUserRequest.nm_email_usuario() != null && !updateUserRequest.nm_email_usuario().trim().isEmpty()) {
                usuario.setNmEmailUsuario(updateUserRequest.nm_email_usuario());
            }
            this.usuarioRepository.save(usuario);
            return new UpdateUserResponse(usuario.getNmUsuario(), usuario.getNmEmailUsuario(), usuario.getNmLinkImagemUsuario());
        } else {
            throw new PermissaoNegadaException("Usuário não te permissão para alterar, pois não é admin deste usuário e nem o próprio usuário!");
        }
    }

    public void atualizarSenhaUsuario(UpdateUserPasswordRequest updateUserPasswordRequest, String id, String token) {
        if(id.trim().isEmpty()) {
            throw  new UsuarioInexistenteException("Id inválido, ou seja, usuário inexistente!");
        }

        //Verificando se o user do token é o mesmo do id (admin n pode alterar senha do user)
        UsuarioEntity usuarioID = this.usuarioRepository.findById(id).orElseThrow(()-> new UsuarioInexistenteException("Usuário inexistente! Id inválido!"));
        String email_user = this.extrairToken(token);
        UsuarioEntity usuarioToken = this.usuarioRepository.findOptionalBynmEmailUsuario(email_user).orElseThrow(()-> new UsuarioInexistenteException("Email do token não corresponde a um user existente!"));

        if(!usuarioID.getCdUsuario().equals(usuarioToken.getCdUsuario())) {
            throw new PermissaoNegadaException("Este usuário não tem permissão de alterar as informações, pois ele não é o usuário afetado!");
        }

        //Validando senha (se era igual a antiga do banco):
        if(!passwordEncoder.matches(updateUserPasswordRequest.nm_senha_antiga(), usuarioID.getNmSenhaUsuario())) {
            throw new SenhaIncorretaException("Senhas não são iguais! Digite a senha correta!");
        } else {
            //Dto já valida formato. Então, preciso fazer o Hash e depois salvar!
            String hash = passwordEncoder.encode(updateUserPasswordRequest.nm_senha_nova());
            usuarioID.setNmSenhaUsuario(hash);
            this.usuarioRepository.save(usuarioID);
        }
    }


    public String extrairToken(String token) {
        String novo_token = token.substring(7); //Removendo Bearer e espaços antes do token
        return this.jwtService.extractUsername(novo_token);
    }

}
