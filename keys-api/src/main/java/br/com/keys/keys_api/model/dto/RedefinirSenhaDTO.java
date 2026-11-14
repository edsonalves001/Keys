package br.com.keys.keys_api.model.dto;

public record RedefinirSenhaDTO (
        String token,
        String senha,
        String senha2
){
}
