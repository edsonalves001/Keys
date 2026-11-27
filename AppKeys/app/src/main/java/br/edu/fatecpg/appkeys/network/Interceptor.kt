package br.edu.fatecpg.appkeys.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class Interceptor(private val tokenManager: TokenManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestOriginal = chain.request()
        val token = tokenManager.getToken()

        var requestNovo: Request

        if (!token.isNullOrEmpty()) {
            requestNovo = requestOriginal.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            requestNovo = requestOriginal
        }

        return chain.proceed(requestNovo)
    }
}