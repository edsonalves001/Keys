package br.com.keys.keys_api.controller;

import br.com.keys.keys_api.model.dto.SolicitarRecuperacaoDTO;
import br.com.keys.keys_api.model.dto.RedefinirSenhaDTO;
import br.com.keys.keys_api.service.RecuperacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EsqueciSenhaController {

    private final RecuperacaoService recuperacaoSenhaService;

    @PostMapping("/esqueci-senha")
    public ResponseEntity<?> solicitarRecuperacao(@RequestBody SolicitarRecuperacaoDTO dto) {
        recuperacaoSenhaService.solicitarRecuperacao(dto.email());
        return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem",
                        "Se o e-mail estiver cadastrado, você receberá um link de recuperação."));
    }

    @GetMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestParam String token) {
        boolean valido = recuperacaoSenhaService.validarToken(token);
        if (!valido) {
            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "mensagem", "Link inválido ou expirado"));
        }
        return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Token válido"));
    }

    @PostMapping("/nova-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaDTO dto) {
        List<String> erros = recuperacaoSenhaService.redefinirSenha(dto);
        if (!erros.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "erros", erros));
        }
        return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Senha atualizada com sucesso"));
    }
}