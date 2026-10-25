package br.com.keys.keys_api.service;

import br.com.keys.keys_api.exception.EmailCadastradoException;
import br.com.keys.keys_api.exception.UsuarioInexistenteException;
import br.com.keys.keys_api.model.dto.LoginRequest;
import br.com.keys.keys_api.model.dto.LoginResponse;
import br.com.keys.keys_api.model.dto.RegisterRequest;
import br.com.keys.keys_api.model.dto.RegisterResponse;
import br.com.keys.keys_api.model.entity.Usuario;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.security.JwtService;
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

    public RegisterResponse criarUser(RegisterRequest registerRequest, String token) {
        //verificar se email é valido:isso
        if (usuarioRepository.findByEmail(registerRequest.nm_email_usuario()).isPresent()) {
            throw new EmailCadastradoException("Email já cadastrado! Tente outro!");
        }

        //Codigo que faz com que o admin seja necessário vai ficar comentado por enquanto

        /* //Pegando cd_admin:
        String email_admin = this.jwtService.extractUsername(token); //admin precisa de token pra criar user
        Usuario admin = usuarioRepository.findByEmailAndChefeIsNull(email_admin).orElseThrow(()-> new UsuarioInexistenteException("Administrador inexistente!"));
*/
        //Criando hash (essa classe veio do método do SecurityConfig):
        String hash = passwordEncoder.encode(registerRequest.nm_senha_usuario());

        Usuario usuario_novo = new Usuario();
        usuario_novo.setCdUsuario(registerRequest.cd_usuario());
        usuario_novo.setNome(registerRequest.nm_usuario());
        usuario_novo.setEmail(registerRequest.nm_email_usuario());
        //usuario_novo.setCdAdmin(admin.getCdAdmin());
        usuario_novo.setImagem("/uploads/usuarios/foto-padrao.png");
        usuario_novo.setSenha(hash);
        //usuario_novo.setEmpresa(admin.getEmpresa());
        usuarioRepository.save(usuario_novo);

        return new RegisterResponse(usuario_novo.getCdUsuario(), usuario_novo.getEmail());
    }

    public LoginResponse logarUser(LoginRequest loginRequest) {
        // 1. AuthenticationManager busca por email E compara senha (via PasswordEncoder)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.nm_email_usuario(), loginRequest.nm_senha_usuario())
        );

        // 2. Gera o token
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        // 3. Devolve
        return new LoginResponse(token);
    }

}
