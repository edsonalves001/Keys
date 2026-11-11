package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.network.TokenManager

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenManager = TokenManager(this)
        val token = tokenManager.getToken()
        if (!token.isNullOrBlank()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_logar = findViewById<Button>(R.id.btn_logar)
        val edt_email = findViewById<EditText>(R.id.edt_email)
        val edt_senha = findViewById<EditText>(R.id.edt_senha)

        btn_logar.setOnClickListener {
            val email = edt_email.text.toString()
            val senha = edt_senha.text.toString()

            if ((email.isNotBlank() && email.isNotEmpty()) && (senha.isNotBlank() && senha.isNotEmpty())) {
                viewModel.fetchLogin(edt_email.text.toString().trim(),edt_senha.text.toString().trim())
            } else {
                Toast.makeText(this, "Um ou mais campos não estão preenchidos", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginResult.observe(this) {
            result -> result.onSuccess { response ->
                Log.d("AUTHORIZATION","Login efetuado com sucesso, token armazenado")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure {
                exception ->
                Log.e("AUTHORIZATION","Falha no login ${exception.message}")

                if (exception.message?.contains("CredenciaisInvalidasException") == true) {
                    Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }
}