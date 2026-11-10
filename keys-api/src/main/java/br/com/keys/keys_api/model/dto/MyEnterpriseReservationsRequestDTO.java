package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record MyEnterpriseReservationsRequestDTO(
        @NotNull(message = "insira o codigo da reserva")
        Integer cdReserva,
        @NotNull(message = "Insira quem fez essa reserva")
        String NomeUser,
        @NotNull(message = "Insira o nome da sala reserva")
        String NomeSala,
        @NotNull(message = "Insira o horário de inicio da reserva")
        LocalTime inicioReserva,
        @NotNull(message = "Insira quando a reserva acabará")
        LocalTime fimReserva
) {
}
