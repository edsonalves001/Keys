package br.edu.fatecpg.appkeys.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class TokenManager(context: Context) {
    //
    private val appContext = context.applicationContext

    //aparentemente essa classe foi descontinuada, porém tem pouqissima informaçãp sobre a nova classe
    //Gera uma 'chave'
    private val masterKey = MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    //Cria um arquivo encriptado
    private val sharedPreferences = EncryptedSharedPreferences.create(appContext,"prefs_secure_auth",masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("Authorization", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("Authorization",null)
    }

    fun removeToken() {
        sharedPreferences.edit().remove("Authorization").apply()
    }
}