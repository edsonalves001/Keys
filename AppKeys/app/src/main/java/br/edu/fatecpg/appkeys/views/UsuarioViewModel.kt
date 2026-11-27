package br.edu.fatecpg.appkeys.views

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.appkeys.model.dto.FindAuthenticatedUserResponse
import br.edu.fatecpg.appkeys.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val usuarioRepository = UsuarioRepository(application)

    private val _usuarioLogadoResult = MutableLiveData<Result<FindAuthenticatedUserResponse>>()
    val usuarioLogadoResult: LiveData<Result<FindAuthenticatedUserResponse>> get() = _usuarioLogadoResult

    fun fetchUsuarioLogado() {
        viewModelScope.launch {
            _usuarioLogadoResult.value = try {
                Result.success(usuarioRepository.getUsuarioLogado())
            } catch (e: Exception) {
                Log.e("USUARIO", "fetchUsuarioLogado: ${e.message}")
                Result.failure(e)
            }
        }
    }
}