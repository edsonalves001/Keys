package br.edu.fatecpg.appkeys.repository

import android.content.Context
import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.AddReservationResponseDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity
import br.edu.fatecpg.appkeys.network.RetrofitClient

class ReservaRepository(private val context: Context) {
    suspend fun getAllReservas(): List<ReservationListResponseDTO> {
        val reservas = RetrofitClient.apiService(context).getAllReservas()

        return reservas
    }

    suspend fun criarReserva(addReservationRequestDTO: AddReservationRequestDTO): AddReservationResponseDTO {
        val resposta = RetrofitClient.apiService(context).criarReserva(addReservationRequestDTO)

        return resposta
    }
}