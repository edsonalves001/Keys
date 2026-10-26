package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.NotBlank;

public record FindAllUsersRequest(
        @NotBlank(message = "É necessário cd_admin para buscar os usuários criados por ele!")
        String cd_admin
) {}
