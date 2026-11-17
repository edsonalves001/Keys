package br.edu.fatecpg.appkeys.repository

import android.content.Context
import br.edu.fatecpg.appkeys.model.dto.FindAuthenticatedUserResponse
import br.edu.fatecpg.appkeys.model.dto.LoginRequestDTO
import br.edu.fatecpg.appkeys.model.dto.LoginResponseDTO
import br.edu.fatecpg.appkeys.network.RetrofitClient
import br.edu.fatecpg.appkeys.network.TokenManager

//Classe repository lida com os dados decidindo se eles vem da API ou banco de dados local
class AuthRepository(private val tokenManager: TokenManager, private val context: Context) {
    //Realiza a chamada da api
    suspend fun login(request: LoginRequestDTO): LoginResponseDTO {
        tokenManager.removeToken()
        tokenManager.removeUserId()

        val resposta = RetrofitClient.apiService(context).loginUser(request) //Resposta da requisição HTTP
        tokenManager.saveToken(resposta.token) //Armazena o token

        val dadosUsuario = RetrofitClient.apiService(context).getUsuarioLogado()
        tokenManager.saveUserId(dadosUsuario.cdUsuario)

        return resposta
    }
}