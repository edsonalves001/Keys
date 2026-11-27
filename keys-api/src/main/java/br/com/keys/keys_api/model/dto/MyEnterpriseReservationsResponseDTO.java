package br.com.keys.keys_api.model.dto;

import br.com.keys.keys_api.enums.StatusReservaEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record MyEnterpriseReservationsResponseDTO(
        Integer cdReserva,
        String NomeUser,
        String NomeSala,
        LocalTime inicioReserva,
        LocalTime fimReserva,
        LocalDateTime infosReserva,
        StatusReservaEnum status
) {
}
