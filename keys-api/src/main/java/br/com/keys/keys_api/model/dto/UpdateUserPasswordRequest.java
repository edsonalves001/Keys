package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserPasswordRequest(
    @NotBlank(message = "Senha antiga é obrigatória!")
    String nm_senha_antiga,
    @NotBlank(message = "Digite um senha válida!")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$",
            message = "Senha fraca! Precisa-se de no mínimo 8 caracteres contendo: " +
                    "1 letra maiúscula, 1 letra minúscula, 1 dígito de 0-9 e 1 caractere especial"
    )
    String nm_senha_nova
) {}
