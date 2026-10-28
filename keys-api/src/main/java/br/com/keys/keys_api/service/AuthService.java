package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.*;
import br.com.keys.keys_api.model.dto.LoginRequestDTO;
import br.com.keys.keys_api.model.dto.LoginResponseDTO;
import br.com.keys.keys_api.model.dto.RegisterRequestDTO;
import br.com.keys.keys_api.model.dto.RegisterResponseDTO;
import br.com.keys.keys_api.model.entity.EmpresaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.EmpresaRepository;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.security.JwtService;
import br.com.keys.keys_api.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager; //autenticar quem buscar fazer login 😁😁😁😁😁
    private final JwtService jwtService; //criar token ao fazer login ou criar conta
    private final PasswordEncoder passwordEncoder; // para fazer hash da senha né, Edson?? 🖕🖕🖕
    private final UsuarioRepository usuarioRepository; //buscar no banco, criar etc
    private final EmpresaRepository empresaRepository; //buscar empresa do adm

    public RegisterResponseDTO criarUser(RegisterRequestDTO registerRequest, String token) { //cria apenas user normal, não adm

        //Email único por user:
        usuarioRepository.findBynmEmailUsuario(registerRequest.nm_email_usuario())
                .ifPresent(u -> { throw new EmailCadastradoException("Email já cadastrado! Tente outro!"); });

        //Pegando cd_admin:
        String email_admin = this.jwtService.extractUsername(token); //admin precisa de token pra criar user
        UsuarioEntity admin = usuarioRepository.findByNmEmailUsuarioAndChefeIsNull(email_admin).orElseThrow(()-> new UsuarioInexistenteException("Administrador inexistente!"));
        if(!admin.getEmpresa().getCd_cnpj_empresa().equals(registerRequest.cd_cnpj_empresa())) {
            throw new AdminOutraEmpresaException("Esse admin é de outra empresa!");
        }

        //Verificando se código está disponível:
        this.usuarioRepository.findById(registerRequest.cd_usuario()).ifPresent(u -> {throw new CodigoUsadoException("Este código já está sendo usado! Tente outro!");});

        //Criando hash (essa classe veio do método do SecurityConfig):
        String hash = passwordEncoder.encode(registerRequest.nm_senha_usuario());

        UsuarioEntity usuario_novo = new UsuarioEntity();
        usuario_novo.setCdUsuario(registerRequest.cd_usuario());
        usuario_novo.setNmUsuario(registerRequest.nm_usuario());
        usuario_novo.setNmEmailUsuario(registerRequest.nm_email_usuario());
        usuario_novo.setChefe(admin);
        usuario_novo.setNmLinkImagemUsuario("/uploads/usuarios/foto-padrao.png");
        usuario_novo.setNmSenhaUsuario(hash);
        usuario_novo.setEmpresa(admin.getEmpresa());
        usuarioRepository.save(usuario_novo);

        return new RegisterResponseDTO(usuario_novo.getCdUsuario(), usuario_novo.getNmEmailUsuario(), "Não faz sentido ter token kkk");
    }

    public LoginResponseDTO logarUser(LoginRequestDTO loginRequest) {
        // 1. AuthenticationManager busca por email E compara senha (via PasswordEncoder)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.nm_email_usuario(), loginRequest.nm_senha_usuario())
        );

        // 2. Gera o token
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        assert userDetails != null;
        String token = jwtService.generateToken(userDetails);

        // 3. Devolve
        return new LoginResponseDTO(token);
    }

    public RegisterResponseDTO criarAdmin(RegisterRequestDTO registerRequestDTO) {

        //Empresa precisa existir para criar adm
        EmpresaEntity empresa = empresaRepository
                .findById(registerRequestDTO.cd_cnpj_empresa())
                .orElseThrow(() -> new EmpresaInexistenteException("Empresa inexistente! Não é possível criar adm!"));

        //Só um admin por empresa
        usuarioRepository.findByEmpresaAndChefeIsNull(empresa)
                .ifPresent(u -> { throw new AdminExistenteException("Já há um admin nessa empresa! Só permitido um por empresa!"); });

        //Email único por user
        usuarioRepository.findBynmEmailUsuario(registerRequestDTO.nm_email_usuario())
                .ifPresent(u -> { throw new EmailCadastradoException("Email já cadastrado! Tente outro!"); });

        //Código único por user
        usuarioRepository.findById(registerRequestDTO.cd_usuario())
                .ifPresent(u -> { throw new CodigoUsadoException("Este código já está em uso! Tente outro!"); });

        //Gerando hash senha:
        String hash = passwordEncoder.encode(registerRequestDTO.nm_senha_usuario());

        //Criando user:
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setCdUsuario(registerRequestDTO.cd_usuario());
        usuario.setNmUsuario(registerRequestDTO.nm_usuario());
        usuario.setNmEmailUsuario(registerRequestDTO.nm_email_usuario());
        usuario.setNmLinkImagemUsuario("/uploads/usuarios/foto-padrao.png");
        usuario.setNmSenhaUsuario(hash);
        usuario.setEmpresa(empresa);

        System.out.println("Teste: " + usuario);
        usuarioRepository.save(usuario);

        //Gerando token:
        UserDetailsImpl ur = new UserDetailsImpl(usuario);
        String token = this.jwtService.generateToken(ur);

        return new RegisterResponseDTO(usuario.getCdUsuario(), usuario.getNmEmailUsuario(), token);
    }
}
