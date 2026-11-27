package br.com.keys.keys_api.model.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AddReservationForListRequestDTO(
        @NotNull(message = "Digite a data de inicio da reserva")
        LocalDate dataReserva,

        @NotNull(message = "Adicione a hora de inicio dessa reserva")
        LocalTime horaInicioReserva,

        @NotNull(message = "Adicione quando essa reserva acabará")
        LocalTime horaFimReserva,

        @NotNull(message = "Adicione quem é o proprietario dessa reserva")
        UserForListReservationRequestDTO proprietario,

        @NotNull(message = "Adicione qual sala será reserva")
        RoomForListReservationDTO salaEntity
) {
}
