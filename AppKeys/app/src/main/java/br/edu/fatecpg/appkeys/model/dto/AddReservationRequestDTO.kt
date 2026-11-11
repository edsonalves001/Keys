package br.edu.fatecpg.appkeys.model.dto

import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.model.entity.usuarioEntity

data class AddReservationRequestDTO (
    val dataReserva: String,
    val horaInicioReserva: String,
    val horaFimReserva: String,
    val propietario: usuarioEntity,
    val salaEntity: salaEntity
)