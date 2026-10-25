package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Necessário adicionar o identificador")
        String cd_usuario,
        @NotBlank(message = "Digite um nome válido!")
        String nm_usuario,
        @Email(message = "Formato de email inválido! Mande um email válido!")
        String nm_email_usuario,
        @NotBlank(message = "Digite um senha válida!")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$",
                message = "Senha fraca! Precisa-se de no mínimo 8 caracteres contendo: " +
                        "1 letra maiúscula, 1 letra minúscula, 1 dígito de 0-9 e 1 caractere especial"
        )
        String nm_senha_usuario,
        @NotBlank(message = "Necessário saber de que empresa é esse usuário/funcionário.")
        String cd_cnpj_empresa
) {}