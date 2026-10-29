package br.com.keys.keys_api.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SemAutenticacaoException implements AuthenticationEntryPoint { //exceção personalizada para evitar resposta genérica

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {
        resp.setStatus(401);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"erro\": \"Não autorizado: Faça login para acessar este recurso.\"}");
    }
}
