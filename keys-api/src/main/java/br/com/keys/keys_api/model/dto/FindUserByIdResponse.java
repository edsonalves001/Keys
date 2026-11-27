package br.com.keys.keys_api.model.dto;

public record FindUserByIdResponse(
        String nm_usuario,
        String nm_email_usuario,
        String nm_link_imagem_usuario
) {}
