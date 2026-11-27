package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.adapter.PerfilAdapter
import br.edu.fatecpg.appkeys.network.TokenManager
import br.edu.fatecpg.appkeys.utils.carregarAvatar

class PerfilActivity : AppCompatActivity() {

    private val viewModel: PerfilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tokenManager = TokenManager(this)

        val btnVoltar = findViewById<ImageButton>(R.id.imgb_voltar)
        val btnCronograma = findViewById<Button>(R.id.btn_cronograma)
        val btnQrScan = findViewById<Button>(R.id.btn_qr_scan)
        val rvReservas = findViewById<RecyclerView>(R.id.rv_reservas)
        val btnDeslogar = findViewById<Button>(R.id.btn_deslogar)
        val imgUsuario = findViewById<ImageView>(R.id.imv_usuario)
        val txvNome = findViewById<TextView>(R.id.txv_titulo_sala)
        val txvEmail = findViewById<TextView>(R.id.txv_titulo_sala2)

        btnVoltar.setOnClickListener { finish() }
        btnQrScan.setOnClickListener {
            val intent = Intent(this, efetivarReservaActivity::class.java)
            startActivity(intent)
        }

        btnCronograma.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val perfilAdapter = PerfilAdapter(emptyList()) { reservaSelecionada ->
            // Adicionar algum dialog que deixe digitar a senha para cancelar a reserva
            viewModel.cancelarReserva(reservaSelecionada.cdReserva, "Senha@123")
        }

        rvReservas.adapter = perfilAdapter
        rvReservas.layoutManager = LinearLayoutManager(this)

        // Dados do usuário logado
        viewModel.fetchUsuarioLogado()

        viewModel.usuarioLogadoResult.observe(this) { result ->
            result.onSuccess { usuario ->
                txvNome.text = usuario.nm_usuario
                txvEmail.text = "Email: ${usuario.nm_email_usuario}"
                imgUsuario.carregarAvatar(usuario.nm_usuario, usuario.nm_link_imagem_usuario)
            }.onFailure { e ->
                Log.e("PERFIL", "Falha ao buscar usuário: ${e.message}")
            }
        }

        // Reservas do usuário
        viewModel.fetchAllReservas(tokenManager.getUserId())

        viewModel.cancelarReservaResul.observe(this) { result ->
            result.onSuccess {
                Log.d("PERFIL", "reserva cancelada com sucesso")
            }
            Toast.makeText(this,"Reserva Cancelada",Toast.LENGTH_SHORT).show()

        }

        viewModel.getALlReservasResult.observe(this) { result ->
            result.onSuccess { reservas ->
                Log.d("PERFIL", "reservas retornadas")
                perfilAdapter.updateAdapter(reservas)
            }.onFailure { exception ->
                Log.e("PERFIL", "Falha ao retornar as reservas: ${exception.message}")
            }
        }
    }
}