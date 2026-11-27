package br.edu.fatecpg.appkeys.repository

import android.content.Context
import br.edu.fatecpg.appkeys.model.dto.FindAuthenticatedUserResponse

import br.edu.fatecpg.appkeys.network.KeysApiService
import br.edu.fatecpg.appkeys.network.RetrofitClient

class UsuarioRepository(context: Context) {

    // AJUSTE esta linha: use o mesmo jeito que o ReservaRepository usa para obter o KeysApiService
    private val apiService: KeysApiService = RetrofitClient.apiService(context)

    suspend fun getUsuarioLogado(): FindAuthenticatedUserResponse =
        apiService.getUsuarioLogado()

}