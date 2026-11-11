package br.edu.fatecpg.appkeys.repository

import android.content.Context
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.network.RetrofitClient
import br.edu.fatecpg.appkeys.network.TokenManager

class SalasRepository(private val context: Context) {
    suspend fun getSalas(): List<salaEntity> {
        val salas = RetrofitClient.apiService(context).getSalas()

        return salas
    }
}