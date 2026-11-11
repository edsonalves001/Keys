package br.edu.fatecpg.appkeys.model.dto

import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.model.entity.usuarioEntity
import java.time.LocalDateTime

data class AddReservationResponseDTO(
    val inicioReserva: LocalDateTime,
    val fimReserva: LocalDateTime,
    val proprietario: usuarioEntity,
    val salaEntity: salaEntity
)
