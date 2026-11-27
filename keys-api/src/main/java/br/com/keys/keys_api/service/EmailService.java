package br.com.keys.keys_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void enviarEmailRecuperacao(String email, String token) {
        String link = frontendUrl + "/nova-senha?token=" + token;
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("edsonpg.alves@gmail.com");
        mensagem.setTo(email);
        mensagem.setSubject("Recuperação de senha - KEYS");

        mensagem.setText("Olá!\n\n" + "Recebemos uma solicitação para redefinir sua senha.\n\n"
                + "Acesse o link abaixo para continuar:\n\n"
                + link + "\n\n"
                + "Este link expira em 20 minutos.\n\n"
                + "Caso não tenha solicitado a recuperação, ignore este e-mail."
        );
        emailSender.send(mensagem);
    }
}