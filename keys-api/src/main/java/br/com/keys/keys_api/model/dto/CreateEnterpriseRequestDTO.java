package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreateEnterpriseRequestDTO(
        @NotBlank(message = "Cnpj inválido! Digite o Cnpj")
        @CNPJ(format = CNPJ.Format.ALPHANUMERIC, message = "Cnpj inválido!")
        String cd_cnpj_empresa,
        @NotBlank(message = "Digite o nome da empresa!")
        String nm_empresa
        )
{ }
