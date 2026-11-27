package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.adapter.ReservasAdapter
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.network.TokenManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: CronogramaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val txvReservasDia = findViewById<TextView>(R.id.txv_reservas_dia)

        val reservasAdapter = ReservasAdapter(emptyList())
        rvReservas.adapter = reservasAdapter
        rvReservas.layoutManager = LinearLayoutManager(this)

        val formatoDia = DateTimeFormatter.ofPattern("EEEE dd/MM", Locale("pt", "BR"))
        var todasReservas = emptyList<ReservationListResponseDTO>()
        var diaSelecionado = LocalDate.now()

        // Filtra pelo dia selecionado e ordena pelo horário de início
        fun filtrarReservas() {
            val reservasDoDia = todasReservas
                .filter { it.dataReserva == diaSelecionado }
                .sortedBy { it.horaInicioReserva }
            reservasAdapter.updateAdapter(reservasDoDia)
        }

        fun selecionarDia(data: LocalDate) {
            diaSelecionado = data
            txvReservasDia.text = data.format(formatoDia).replaceFirstChar { it.uppercase() }
            filtrarReservas()
        }

        // Mostra o dia de hoje ao abrir a tela
        selecionarDia(LocalDate.now())

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // CalendarView envia o mês de 0 a 11, LocalDate espera de 1 a 12
            selecionarDia(LocalDate.of(year, month + 1, dayOfMonth))
        }

        btnReservar.setOnClickListener {
            startActivity(Intent(this, SalasDisponiveisActivity::class.java))
        }

        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        val barLauncher = registerForActivityResult(ScanContract()) { result ->
            val scannedData = result.contents
            if (scannedData != null) {
                viewModel.efetivarReserva(scannedData)
            } else {
                Toast.makeText(this, "Scan cancelado", Toast.LENGTH_SHORT).show()
            }
        }

        botaoQR.setOnClickListener {
            val options = ScanOptions()
            options.setBeepEnabled(true) // Beep de confirmação após a leitura
            options.setOrientationLocked(true) // Mantém a câmera fixa
            options.setCaptureActivity(efetivarReservaActivity::class.java) // Activity do leitor

            barLauncher.launch(options)
        }

        viewModel.fetchAllReservas()

        viewModel.getALlReservasResult.observe(this) { result ->
            result.onSuccess { reservas ->
                Log.d("CRONOGRAMA", "reservas retornadas")
                todasReservas = reservas
                filtrarReservas()
            }.onFailure { exception ->
                Log.e("CRONOGRAMA", "Falha ao retornar as reservas: ${exception.message}")
            }
        }
    }
}