package br.com.keys.keys_api.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class SemPermissaoException implements AccessDeniedHandler { //personalizei a exceção pra entender, pois mts vezes vem exceção genérica do maledito Spring Security e isso tava me irritando!!!!!!!!!!!!!

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        resp.setStatus(403);
        resp.setContentType("application/json");
        resp.getWriter().write("{\\\"erro\\\": \\\"Acesso negado: Você não possui permissão para esta rota.\\\"}");
    }
}
