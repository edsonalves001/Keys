package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.repository.ReservaRepository
import br.edu.fatecpg.appkeys.repository.SalasRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CronogramaViewModel(application: Application): AndroidViewModel(application)  {
    private val tokenManager = TokenManager(application)
    private val salaRepository = SalasRepository(application)
    private val reservaRepository = ReservaRepository(application)

    private val _getSalasResult = MutableLiveData<Result<List<salaEntity>>>()
    val getSalasResult: LiveData<Result<List<salaEntity>>> get() = _getSalasResult

    fun fetchSalas() {
        viewModelScope.launch {
            try {
                val response = salaRepository.getSalas()
                _getSalasResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("CRONOGRAMA", "fetchSalas: Internal server error")
                        val tokenManager = TokenManager(getApplication())
                        tokenManager.removeToken()
                    } else -> {
                        Log.d("CRONOGRAMA", "fetchSalas: Unhandled error")
                    }
                }

                _getSalasResult.value = Result.failure(e)
            } catch (e: IOException) {
                e.printStackTrace()
                _getSalasResult.value = Result.failure(e)
            }
        }
    }

    private val _getAllReservasResult = MutableLiveData<Result<List<ReservationListResponseDTO>>>()
    val getALlReservasResult: LiveData<Result<List<ReservationListResponseDTO>>> get() = _getAllReservasResult

    fun fetchAllReservas() {
        viewModelScope.launch {
            try {
                val response = reservaRepository.getAllReservas()
                _getAllReservasResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("RESERVAS", "fetchLogin: Internal server error")
                    } else -> {
                    Log.d("RESERVAS", "fetchLogin: Unhandled error")
                }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _getAllReservasResult.value = Result.failure(e)
            }
        }
    }
}