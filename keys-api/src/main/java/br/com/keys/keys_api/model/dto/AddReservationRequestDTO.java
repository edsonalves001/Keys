package br.com.keys.keys_api.model.dto;

import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AddReservationRequestDTO(
        @NotNull(message = "Digite a data de inicio da reserva")
        LocalDate dataReserva,

        @NotNull(message = "Adicione a hora de inicio dessa reserva")
        LocalTime horaInicioReserva,

        @NotNull(message = "Adicione quando essa reserva acabará")
        LocalTime horaFimReserva,

        @NotNull(message = "Adicione quem é o proprietario dessa reserva")
        UsuarioEntity proprietario,

        @NotNull(message = "Adicione qual sala será reserva")
        SalaEntity salaEntity

) {}
