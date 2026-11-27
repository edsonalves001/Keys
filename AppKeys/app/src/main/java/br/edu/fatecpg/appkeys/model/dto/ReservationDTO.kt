package br.edu.fatecpg.appkeys.model.dto

import br.edu.fatecpg.appkeys.enums.StatusReservaEnum
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ReservationDTO (
    @SerializedName("cdreserva")
    val cdReserva: Int,
    val fimReserva: String?,
    val inicioReserva: String?,
    val statusReserva: StatusReservaEnum,
    val nomeUser: String,
    val nomeSala: String
)