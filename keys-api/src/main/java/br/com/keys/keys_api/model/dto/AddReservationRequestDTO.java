package br.com.keys.keys_api.model.dto;

import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AddReservationRequestDTO(
        @NotNull(message = "Digite a data de inicio da reserva")
        LocalDateTime inicioReserva,
        @NotNull(message = "Adicione quando essa reserva acabará")
        LocalDateTime fimReserva,

        @NotNull(message = "Adicione quem é o proprietario dessa reserva")
        UsuarioEntity proprietario,

        @NotNull(message = "Adicione qual sala será reserva")
        SalaEntity salaEntity

) {}
