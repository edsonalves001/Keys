package br.edu.fatecpg.appkeys.model.entity

import br.edu.fatecpg.appkeys.enums.StatusReservaEnum
import java.time.LocalDateTime

data class reservaEntity (
    val cdReserva: String = "",
    val inicioReserva: String,
    val fimReserva: String,
    val proprietario: usuarioEntity,
    val salaEntity: salaEntity,
    val statusReserva: StatusReservaEnum
)