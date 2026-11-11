package br.edu.fatecpg.appkeys.network

import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.AddReservationResponseDTO
import br.edu.fatecpg.appkeys.model.dto.LoginRequestDTO
import br.edu.fatecpg.appkeys.model.dto.LoginResponseDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity
import br.edu.fatecpg.appkeys.model.entity.salaEntity

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface KeysApiService {
    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest:LoginRequestDTO): LoginResponseDTO

    @GET("api/salas")
    suspend fun getSalas(): List<salaEntity>

    @GET("api/reservas")
    suspend fun getAllReservas(): List<ReservationListResponseDTO>

    @POST("api/criarReserva")
    suspend fun criarReserva(@Body addReservationRequestDTO: AddReservationRequestDTO): AddReservationResponseDTO
}