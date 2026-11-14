package br.com.keys.keys_api.model.dto;

import br.com.keys.keys_api.enums.StatusReservaEnum;

import java.time.LocalDateTime;

public record ReservationDTO(
        Integer cdreserva,
        LocalDateTime fimReserva,
        LocalDateTime inicioReserva,
        StatusReservaEnum statusReserva,

        String nomeUser,
        String nomeSala
) {


}
