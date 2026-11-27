package br.com.keys.keys_api.service;

import br.com.keys.keys_api.model.entity.UsuarioEntity;
import br.com.keys.keys_api.repository.UsuarioRepository;
import br.com.keys.keys_api.model.dto.RedefinirSenhaDTO;

import io.jsonwebtoken.JwtException;

import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecuperacaoService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void solicitarRecuperacao(String email) {

        UsuarioEntity usuario = usuarioRepository.findBynmEmailUsuario(email);

        if (usuario == null) {
            return;
        }
        String token = tokenService.gerarToken(email);
        emailService.enviarEmailRecuperacao(email, token);
    }

    public void solicitarAlteracao(String senha, Authentication auth){

    }

    public boolean validarToken(String token) {
        try {
            String email = tokenService.validarToken(token);
            UsuarioEntity usuario = usuarioRepository.findBynmEmailUsuario(email);
            return usuario != null;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public List<String> redefinirSenha(RedefinirSenhaDTO dto) {
        List<String> erros = new ArrayList<>();
        String email;
        try {
            email = tokenService.validarToken(dto.token());

        } catch (JwtException | IllegalArgumentException ex) {
            erros.add("Link de recuperação inválido ou expirado");
            return erros;
        }

        UsuarioEntity usuario = usuarioRepository.findBynmEmailUsuario(email);

        if (usuario == null) {
            erros.add("Usuário não encontrado");
            return erros;
        }

        String senha = dto.senha();
        String senha2 = dto.senha2();

        if (senha == null || senha.length() < 8) {
            erros.add("A senha deve ter pelo menos 8 caracteres");
        } else {
            //Verificação da senha via Regex
            if (!senha.matches(".*[A-Z].*")) {
                erros.add("A senha precisa ter letra maiúscula");
            }

            if (!senha.matches(".*[a-z].*")) {
                erros.add("A senha precisa ter letra minúscula");
            }

            if (!senha.matches(".*\\d.*")) {
                erros.add("A senha precisa ter número");
            }

            if (!senha.matches(".*[@#$%^&+=!].*")) {
                erros.add("A senha precisa ter caractere especial");
            }
        }

        if (senha2 == null || !senha2.equals(senha)) {
            erros.add("As senhas não coincidem");
        }

        if (!erros.isEmpty()) {
            return erros;
        }

        usuario.setNmSenhaUsuario(passwordEncoder.encode(senha));
        usuarioRepository.save(usuario);
        return erros;
    }
}