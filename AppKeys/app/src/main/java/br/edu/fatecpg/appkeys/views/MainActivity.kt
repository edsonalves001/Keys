package br.edu.fatecpg.appkeys.views

import android.content.Intent
import br.edu.fatecpg.appkeys.network.RetrofitClient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.adapter.ReservasAdapter
import br.edu.fatecpg.appkeys.network.KeysApiService
import br.edu.fatecpg.appkeys.network.TokenManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: CronogramaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        val tokenManager = TokenManager(this)

        val botaoQR = findViewById<Button>(R.id.btn_qr_scan)
        val btnPerfil = findViewById<Button>(R.id.btn_perfil)
        val btnCronograma = findViewById<Button>(R.id.btn_cronograma)
        val btnReservar = findViewById<Button>(R.id.btn_reservar)
        val rvReservas = findViewById<RecyclerView>(R.id.rv_Reservas)

        var reservasAdapter = ReservasAdapter(emptyList())
        rvReservas.adapter = reservasAdapter
        rvReservas.layoutManager = LinearLayoutManager(this)

        //btnCronograma.color(yellow)

        btnReservar.setOnClickListener {
            val intent = Intent(this, SalasDisponiveisActivity::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        botaoQR.setOnClickListener {
            val options = ScanOptions()
            options.setBeepEnabled(true) //Faz com que após a leitura do codigo qr, saia um beep de confirmação
            options.setOrientationLocked(true) //Mantém a posição da camera fixa
            options.setCaptureActivity(efetivarReservaActivity::class.java)//Diz qual activity vai abrir o leitor

            barLauncher.launch(options)
        }

        viewModel.fetchAllReservas()

        viewModel.getALlReservasResult.observe(this) {
            result -> result.onSuccess { reservas ->
                Log.d("CRONOGRAMA","reservas retornadas")

                reservasAdapter.updateAdapter(reservas)
            }.onFailure {
                exception -> Log.e("CRONOGRAMA", "Falha ao retornar as reservas: ${exception.message}")
            }
        }
    }

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
    //Cria uma caixa de mensagem que caso o conteudo do qr code não esteja vazio, ele abre essa caixa de
        //mensagem na main activity, o botão continuar fecha a caixa de mensagem
        if (result.contents != null) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Reserva efetivada")
            builder.setMessage("Retire a chave em até 2 minutos antes da escotilha fechar " + result.contents)
            builder.setPositiveButton("Continuar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}