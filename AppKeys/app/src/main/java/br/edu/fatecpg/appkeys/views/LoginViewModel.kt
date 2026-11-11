package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.dto.LoginRequestDTO
import br.edu.fatecpg.appkeys.model.dto.LoginResponseDTO
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.repository.AuthRepository
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val repository = AuthRepository(tokenManager,application)

    private val _loginResult = MutableLiveData<Result<LoginResponseDTO>>()
    val loginResult: LiveData<Result<LoginResponseDTO>> get() = _loginResult
    
    fun fetchLogin(nm_email_usuario: String, nm_senha_usuario: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequestDTO(nm_email_usuario, nm_senha_usuario)
                val response = repository.login(request)
                _loginResult.value = Result.success(response)
            } catch (e: retrofit2.HttpException) {
                when(e.code()) {
                    500 -> {
                        Log.d("AUTHORIZATION", "fetchLogin: Internal server error")
                    } else -> {
                        Log.d("AUTHORIZATION", "fetchLogin: Unhandled error")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _loginResult.value = Result.failure(e)
            }
        }
    }
}