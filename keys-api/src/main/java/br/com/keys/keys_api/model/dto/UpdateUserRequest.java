package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        String nm_usuario,
        String nm_email_usuario
) {}
