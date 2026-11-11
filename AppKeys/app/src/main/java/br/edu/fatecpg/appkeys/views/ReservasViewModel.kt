package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.AddReservationResponseDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.repository.ReservaRepository
import kotlinx.coroutines.launch
import java.io.IOException

class ReservasViewModel(application: Application): AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val repository = ReservaRepository(application)

    private val _getReservaResult = MutableLiveData<Result<AddReservationResponseDTO>>()
    val getReservaResult: LiveData<Result<AddReservationResponseDTO>> get() = _getReservaResult

    fun fetchCriarReserva(addReservationRequestDTO: AddReservationRequestDTO) {
        viewModelScope.launch {
            try {
                val response = repository.criarReserva(addReservationRequestDTO)
                _getReservaResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("RESERVAS", "fetchReservar: Internal server error")
                    } else -> {
                    Log.d("RESERVAS", "fetchReservar: Unhandled error")
                }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _getReservaResult.value = Result.failure(e)
            }
        }
    }
}