package br.edu.fatecpg.appkeys.model.dto

import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.model.entity.usuarioEntity

data class Proprietario(
    val cdUsuario: String?
)

data class Sala(
    val cd: Int
)
data class AddReservationRequestDTO (
    val dataReserva: String,
    val horaInicioReserva: String,
    val horaFimReserva: String,
    val proprietario: Proprietario,
    val salaEntity: Sala
)