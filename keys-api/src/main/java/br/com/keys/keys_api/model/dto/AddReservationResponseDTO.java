package br.com.keys.keys_api.model.dto;

import br.com.keys.keys_api.model.entity.SalaEntity;
import br.com.keys.keys_api.model.entity.UsuarioEntity;

import java.time.LocalDateTime;

public record AddReservationResponseDTO(LocalDateTime inicioReserva, LocalDateTime fimReserva,
                                        UsuarioEntity proprietario, SalaEntity salaEntity) {
}
