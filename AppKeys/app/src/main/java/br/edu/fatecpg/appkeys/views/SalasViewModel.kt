package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.repository.SalasRepository
import kotlinx.coroutines.launch
import java.io.IOException

class SalasViewModel(application: Application): AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val repository = SalasRepository(application)

    private val _getAllSalasResult = MutableLiveData<Result<List<salaEntity>>>()
    val getAllSalasResult: LiveData<Result<List<salaEntity>>> get() = _getAllSalasResult

    fun fetchAllSalas() {
        viewModelScope.launch {
            try {
                val response = repository.getSalas()
                _getAllSalasResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("SALAS", "fetchAllSalas: Internal server error")
                    } else -> {
                        Log.d("SALAS", "fetchAllSalas: Unhandled error")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _getAllSalasResult.value = Result.failure(e)
            }
        }
    }
}