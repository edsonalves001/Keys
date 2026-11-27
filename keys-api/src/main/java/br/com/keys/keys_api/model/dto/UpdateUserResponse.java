package br.com.keys.keys_api.model.dto;

public record UpdateUserResponse(
        String nm_usuario,
        String nm_email_usuario,
        String nm_imagem_usuario
) {}
