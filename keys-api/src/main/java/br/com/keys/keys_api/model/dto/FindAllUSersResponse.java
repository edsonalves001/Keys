package br.com.keys.keys_api.model.dto;

public record FindAllUSersResponse(
        String id,
        String nome,
        String email_user,
        String nm_link_imagem_usuario
    )
{}
