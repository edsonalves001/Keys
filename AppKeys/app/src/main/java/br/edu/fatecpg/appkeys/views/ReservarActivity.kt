package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.dto.AddReservationRequestDTO
import br.edu.fatecpg.appkeys.model.dto.Proprietario
import br.edu.fatecpg.appkeys.model.dto.Sala
import br.edu.fatecpg.appkeys.model.entity.salaEntity
import android.widget.ImageView
import br.edu.fatecpg.appkeys.utils.carregarAvatar
import br.edu.fatecpg.appkeys.network.TokenManager

class ReservarActivity : AppCompatActivity() {
    val viewModel: ReservasViewModel by viewModels()
    val tokenManager by lazy { TokenManager(application) }
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pickerInicio = findViewById<EditText>(R.id.edtm_timepicker_hora_inicio)
        val pickerFinal = findViewById<EditText>(R.id.edtm_timepicker_hora_fim)
        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)

        val imgUsuario = findViewById<ImageView>(R.id.imgv_usuario)
        val txvNomeUsuario = findViewById<TextView>(R.id.txv_quemreservou)
        usuarioViewModel.fetchUsuarioLogado()

        usuarioViewModel.usuarioLogadoResult.observe(this) { result ->
            result.onSuccess { usuario ->
                txvNomeUsuario.text = "Reservado por: ${usuario.nm_usuario}"
                imgUsuario.carregarAvatar(usuario.nm_usuario, usuario.nm_link_imagem_usuario)
            }.onFailure { e ->
                Log.e("RESERVAS", "Falha ao buscar usuário: ${e.message}")
            }
        }

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this,"Operação cancelada",Toast.LENGTH_SHORT).show()
        }

        val sala = intent.getSerializableExtra("salaSelecionada") as? salaEntity

        sala?.let {
            val txvTituloSala = findViewById<TextView>(R.id.txv_titulo_sala)
            txvTituloSala?.text = it.nome
        }

        btnConfirmar.setOnClickListener {
            val horaInicio = pickerInicio.text.toString()
            val horaFim = pickerFinal.text.toString()

            if (horaInicio.isEmpty() || horaFim.isEmpty()) {
                Toast.makeText(this, "Ambos horarios devem estar selecionados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = java.time.LocalDate.now().toString()

            val request = AddReservationRequestDTO(
                dataReserva = data,
                horaInicioReserva = "$horaInicio:00",
                horaFimReserva = "$horaFim:00",
                proprietario = Proprietario(tokenManager.getUserId()),
                salaEntity = Sala(sala?.cd ?: return@setOnClickListener)
            )

            viewModel.fetchCriarReserva(request)
        }

        viewModel.getReservaResult.observe(this) {
            result -> result.onSuccess {
                Log.d("RESERVAS","Sala reservada com sucesso")
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            Toast.makeText(this,"Sala reservada com sucesso",Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }.onFailure {
                exception -> Log.d("RESERVAS", "Falha ao reservar sala: ${exception.message} ")
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }

        //Magia além de compreensão
        fun generateSlots(minutosInicial: Int, minutosFinal: Int): List<String> =
            (minutosInicial..minutosFinal step 15).map { String.format("%02d:%02d", it / 60, it % 60) }

        fun paraMinutos(hhmm: String): Int {
            val (h, m) = hhmm.split(":").map { it.toInt() }
            return h * 60 + m
        }

        fun showSeletor(titulo: String, opcoes: List<String>, onClick: (String) -> Unit) {
            if (opcoes.isEmpty()) {
                Toast.makeText(this, "Nenhum horário disponível", Toast.LENGTH_SHORT).show()
                return
            }
            android.app.AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(opcoes.toTypedArray()) { _, i -> onClick(opcoes[i]) }
                .show()
        }

        pickerInicio.setOnClickListener {
            val opcoes = generateSlots(0, 23 * 60 + 30)
            showSeletor("Horário de início", opcoes) { horarioReserva ->
                pickerInicio.setText(horarioReserva)

                val fimAtual = pickerFinal.text.toString()
                if (fimAtual.isNotEmpty() && paraMinutos(fimAtual) < paraMinutos(horarioReserva) + 15) {
                    pickerFinal.setText("")
                }
            }
        }

        pickerFinal.setOnClickListener {
            val inicio = pickerInicio.text.toString()
            if (inicio.isEmpty()) {
                Toast.makeText(this, "Selecione o horário de início", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val opcoes = generateSlots(paraMinutos(inicio) + 15, 23 * 60 + 45)
            showSeletor("Horário de término", opcoes) { horarioReserva ->
                pickerFinal.setText(horarioReserva)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RESERVAS", "ReservarActivity destruída", Throwable())
    }
}