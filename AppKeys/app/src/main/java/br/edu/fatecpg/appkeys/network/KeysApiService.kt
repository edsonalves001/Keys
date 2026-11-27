package br.edu.fatecpg.appkeys.network

import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.AddReservationResponseDTO
import br.edu.fatecpg.appkeys.model.dto.FindAuthenticatedUserResponse
import br.edu.fatecpg.appkeys.model.dto.LoginRequestDTO
import br.edu.fatecpg.appkeys.model.dto.LoginResponseDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface KeysApiService {
    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest:LoginRequestDTO): LoginResponseDTO

    @GET("api/salas")
    suspend fun getSalas(): List<salaEntity>

    @GET("api/reservas")
    suspend fun getAllReservas(): List<ReservationListResponseDTO>

    @GET("api/VerReservaDesseUsuario/{cd}")
    suspend fun getMinhasReservas(@Path("cd") cd: String?): List<ReservationDTO>

    @POST("api/criarReserva")
    suspend fun criarReserva(@Body addReservationRequestDTO: AddReservationRequestDTO): AddReservationResponseDTO

    @GET("api/usuario/perfil")
    suspend fun getUsuarioLogado(): FindAuthenticatedUserResponse

    @DELETE("api/ApagarReserva")
    suspend fun apagarReserva(@Query("id") id: Int, @Query("senha") senha: String): String

    @POST("api/EfetivarReserva/{cnpjEmpresa}")
    suspend fun efetivarReserva(@Path(value = "cnpjEmpresa") cnpjEmpresa: String): Response<Unit>

}