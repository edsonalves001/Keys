package br.edu.fatecpg.appkeys.views

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import br.edu.fatecpg.appkeys.model.entity.usuarioEntity

class ReservarActivity : AppCompatActivity() {
    val viewModel: ReservasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pickerInicio = findViewById<EditText>(R.id.edtm_timepicker_hora_inicio)
        val pickerFinal = findViewById<EditText>(R.id.edtm_timepicker_hora_fim)
        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar)

        val sala = intent.getSerializableExtra("salaSelecionada") as? salaEntity

        sala?.let {
            val txvTituloSala = findViewById<TextView>(R.id.txv_nome_sala)
            txvTituloSala.text = it.nome
        }

        btnConfirmar.setOnClickListener {
            val horaInicio = pickerInicio.text.toString()
            val horaFim = pickerFinal.text.toString()

            if (horaInicio.isEmpty() || horaFim.isEmpty()) {
                Toast.makeText(this, "Ambos horarios devem estar selecionados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //ISSO PRECISA PEGAR A DATA DA PORRA DO CALENDARIO DE ALGUMA FORMA
            val data = java.time.LocalDate.now().toString()

            val request = AddReservationRequestDTO(
                dataReserva = data,
                horaInicioReserva = "$horaInicio:00",
                horaFimReserva = "$horaFim:00",
                //propietario = usuarioEntity(//como caralhos eu pego o usuario atual??)
                salaEntity = sala ?: return@setOnClickListener
            )

            viewModel.fetchCriarReserva(request)
        }

        viewModel.getReservaResult.observe(this) {
            result -> result.onSuccess { response -> finish() }.onFailure { exception -> Log.d("RESERVAS", "Falha ao reservar sala: ${exception.message} ") }
        }

        pickerInicio.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)

            android.app.TimePickerDialog(this, {_, horaSelecionada, minutoSelecionado ->
                val formattedTime = String.format("%02d:%02d", horaSelecionada, minutoSelecionado)
                pickerInicio.setText(formattedTime)
            }, hour, minute, true).show()
        }

        pickerFinal.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)

            android.app.TimePickerDialog(this, {_, horaSelecionada, minutoSelecionado ->
                val formattedTime = String.format("%02d:%02d", horaSelecionada, minutoSelecionado)
                pickerFinal.setText(formattedTime)
            }, hour, minute, true).show()
        }
    }
}