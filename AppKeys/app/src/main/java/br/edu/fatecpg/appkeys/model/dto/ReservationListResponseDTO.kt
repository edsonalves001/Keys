package br.edu.fatecpg.appkeys.model.dto

import java.time.LocalDate
import java.time.LocalTime

data class ReservationListResponseDTO (
    val nomeSala: String,
    val linkImagemUsuario: String,
    val dataReserva: LocalDate?,
    val horaInicioReserva: LocalTime?,
    val horaFimReserva: LocalTime?,
    val proprietario: String
)