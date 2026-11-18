package br.edu.fatecpg.appkeys.model.dto

import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.model.entity.usuarioEntity
import java.time.LocalDateTime

data class AddReservationResponseDTO(
    val inicioReserva: String?,
    val fimReserva: String?,
    val proprietario: usuarioEntity,
    val salaEntity: salaEntity
)
