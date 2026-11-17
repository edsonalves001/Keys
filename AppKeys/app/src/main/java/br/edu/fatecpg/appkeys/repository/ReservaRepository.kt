package br.edu.fatecpg.appkeys.repository

import android.content.Context
import android.util.Log
import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.AddReservationResponseDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity
import br.edu.fatecpg.appkeys.network.RetrofitClient

class ReservaRepository(private val context: Context) {
    suspend fun getAllReservas(): List<ReservationListResponseDTO> {
        val reservas = RetrofitClient.apiService(context).getAllReservas()

        return reservas
    }

    suspend fun getMinhasReservas(userCd: String?): List<ReservationDTO> {
        val reservas = RetrofitClient.apiService(context).getMinhasReservas(userCd)
        Log.d("DEBUG","getMinhasReservas: Reservas Fetched: $reservas")

        return reservas
    }

    suspend fun criarReserva(addReservationRequestDTO: AddReservationRequestDTO): AddReservationResponseDTO {
        val resposta = RetrofitClient.apiService(context).criarReserva(addReservationRequestDTO)

        return resposta
    }

    suspend fun cancelarReserva(id: Int, senha: String): String {
        val resposta = RetrofitClient.apiService(context).apagarReserva(id, senha)
        Log.d("DEBUG","cancelarReserva: Reservas Fetched: $resposta")

        return resposta
    }
}