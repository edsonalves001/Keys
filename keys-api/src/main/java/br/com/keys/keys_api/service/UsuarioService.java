package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.SenhaIncorretaException;
import br.com.keys.keys_api.exception.UsuarioInexistenteException;
import br.com.keys.keys_api.model.dto.*;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
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
    private PasswordEncoder passwordEncoder; //verificar se a senha hash é igual a outra senha. Compara duas hash


    public List<FindAllUSersResponse> buscarTodosUsuarios(FindAllUsersRequest findAllUsersRequest, String token) {

        //Verifica se é adm o dono do token:
        String email_user = jwtService.extractUsername(token);
        UsuarioEntity usuario = this.usuarioRepository.findByNmEmailUsuarioAndChefeIsNull(email_user).orElseThrow(()-> new UsuarioInexistenteException("Admin inexistente!"));

        //Busca users feitos por ele:
        List<UsuarioEntity> usuarios = this.usuarioRepository.findByChefe(usuario.getChefe());
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
            //Buscando pra ver se usuario existe
            String email_user = jwtService.extractUsername(token);
            this.usuarioRepository.findBynmEmailUsuario(email_user).orElseThrow(()-> new UsuarioInexistenteException("Usuário do token é inexistente!!!!"));

            //Excluindo
            this.usuarioRepository.deleteById(id);
        }
    }


    /*
    public UpdateSenhaByEmailResponse updateSenhaByEmail(UpdateSenhaByEmailRequest updateSenhaByEmailRequest) {
        if (updateSenhaByEmailRequest.nm_senha_nova().trim().equals(updateSenhaByEmailRequest.nm_senha_nova_confirmada().trim())) {

        }
    }
    */


    public UpdateUserResponse atualizarInformacoesUsuario(String id, UpdateUserRequest updateUserRequest) {
        UsuarioEntity usuario = this.usuarioRepository.findById(id).orElseThrow(()-> new UsuarioInexistenteException("Usuário não encontrado!"));

        if(updateUserRequest.nm_usuario() != null && !updateUserRequest.nm_usuario().trim().isEmpty()) {
            usuario.setNmUsuario(updateUserRequest.nm_usuario());
        }
        if(updateUserRequest.nm_email_usuario() != null && !updateUserRequest.nm_email_usuario().trim().isEmpty()) {
            usuario.setNmUsuario(updateUserRequest.nm_email_usuario());
        }
        if(updateUserRequest.nm_link_imagem_usuario() != null && !updateUserRequest.nm_link_imagem_usuario().trim().isEmpty()) {
            usuario.setNmUsuario(updateUserRequest.nm_link_imagem_usuario());
        }
        this.usuarioRepository.save(usuario);
        return new UpdateUserResponse(usuario.getNmUsuario(), usuario.getNmEmailUsuario(), usuario.getNmLinkImagemUsuario());
    }

    public void atualizarSenhaUsuario(UpdateUserPasswordRequest updateUserPasswordRequest, String id) {
        if(id.trim().isEmpty()) {
            throw  new UsuarioInexistenteException("Id inválido, ou seja, usuário inexistente!");
        }

        UsuarioEntity usuario = this.usuarioRepository.findById(id).orElseThrow(()-> new UsuarioInexistenteException("Usuário inexistente! Id inválido!"));
        if(!passwordEncoder.matches(updateUserPasswordRequest.nm_senha_antiga(), usuario.getNmSenhaUsuario())) {
            throw new SenhaIncorretaException("Senhas não são iguais! Digite a senha correta!");
        } else {
            usuario.setNmSenhaUsuario(updateUserPasswordRequest.nm_senha_nova());
            this.usuarioRepository.save(usuario);
        }
    }




}
