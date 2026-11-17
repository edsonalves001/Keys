package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.dto.ReservationDTO
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.repository.ReservaRepository
import kotlinx.coroutines.launch
import java.io.IOException

class PerfilViewModel(application: Application): AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val reservaRepository = ReservaRepository(application)

    private val _getAllReservasResult = MutableLiveData<Result<List<ReservationDTO>>>()
    val getALlReservasResult: LiveData<Result<List<ReservationDTO>>> get() = _getAllReservasResult

    fun fetchAllReservas(userCd: String?) {
        viewModelScope.launch {
            try {
                val response = reservaRepository.getMinhasReservas(userCd)
                _getAllReservasResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("RESERVAS", "fetchAllReservas: Internal server error")
                        Log.d("DEBUG","fetchAllReservas, CD do Usuário: $userCd")
                    } else -> {
                    Log.d("RESERVAS", "fetchAllReservas: Unhandled error")
                }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _getAllReservasResult.value = Result.failure(e)
            }
        }
    }

    private val _cancelarReservaResult = MutableLiveData<Result<String>>()
    val cancelarReservaResul: LiveData<Result<String>> get() = _cancelarReservaResult

    fun cancelarReserva(id: Int, senha: String) {
        viewModelScope.launch {
            try {
                val response = reservaRepository.cancelarReserva(id, senha)
                _cancelarReservaResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when (e.code()) {
                    500 -> {
                        Log.d("RESERVAS", "fetchLogin: Internal server error")
                        _cancelarReservaResult.value = Result.failure(e)
                    }

                    else -> {
                        Log.d("RESERVAS", "fetchLogin: Unhandled error")
                        _cancelarReservaResult.value = Result.failure(e)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _cancelarReservaResult.value = Result.failure(e)
            }
        }
    }
}