package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.adapter.PerfilAdapter
import br.edu.fatecpg.appkeys.network.TokenManager

class PerfilActivity : AppCompatActivity() {

    private val viewModel: PerfilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tokenManager = TokenManager(this)

        val btnVoltar = findViewById<ImageButton>(R.id.imgb_voltar)
        val btnCronograma = findViewById<Button>(R.id.btn_cronograma)
        val rvReservas = findViewById<RecyclerView>(R.id.rv_reservas)
        val btnDeslogar = findViewById<Button>(R.id.btn_deslogar)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnCronograma.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var perfilAdapter = PerfilAdapter(emptyList(), { reservaSelecionada ->
            //Adicionar algum dialog que deixe digitar a senha para cancelar a reserva
            viewModel.cancelarReserva(reservaSelecionada.cdReserva,"Senha@123")
        })
        rvReservas.adapter = perfilAdapter
        rvReservas.layoutManager = LinearLayoutManager(this)

        viewModel.fetchAllReservas(tokenManager.getUserId())

        viewModel.getALlReservasResult.observe(this) {
                result -> result.onSuccess { reservas ->
                Log.d("CRONOGRAMA","reservas retornadas")

                perfilAdapter.updateAdapter(reservas)
            }.onFailure {
                    exception -> Log.e("CRONOGRAMA", "Falha ao retornar as reservas: ${exception.message}")
            }
        }

    }
}