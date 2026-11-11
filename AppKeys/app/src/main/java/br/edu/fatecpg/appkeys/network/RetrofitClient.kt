package br.edu.fatecpg.appkeys.network

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.102:8080/"

    fun apiService(context: Context): KeysApiService {
        val tokenManager = TokenManager(context)

        val clientOkHttp = OkHttpClient.Builder().addInterceptor(Interceptor(tokenManager)).build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .create()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientOkHttp)
            .baseUrl(BASE_URL)
            .build()
            .create(KeysApiService::class.java)
    }
}