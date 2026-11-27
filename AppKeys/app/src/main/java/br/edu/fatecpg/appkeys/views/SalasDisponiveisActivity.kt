package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.adapter.salasadapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.getValue

class SalasDisponiveisActivity : AppCompatActivity() {

    private val viewModel: SalasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_salas_disponiveis)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fabSair = findViewById<FloatingActionButton>(R.id.fab_sair)

        val rvSalas = findViewById<RecyclerView>(R.id.rv_salasDisponiveis)
        var salasAdapter = salasadapter(emptyList()) { salaSelecionada ->
            val intent = Intent(this, ReservarActivity::class.java).apply {
                putExtra("salaSelecionada", salaSelecionada)
            }
            startActivity(intent)
        }
        rvSalas.adapter = salasAdapter
        rvSalas.layoutManager = LinearLayoutManager(this)

        viewModel.fetchAllSalas()

        viewModel.getAllSalasResult.observe(this) {
            result -> result.onSuccess { salas ->
                Log.d("SALAS", "Lista de salas retornada com sucesso")
                salasAdapter.updateAdapter(salas)
            }.onFailure {
                exception ->
                Log.d("SALAS", "Falha ao retornar salas: ${exception.message} ")
            }
        }

        fabSair.setOnClickListener {
            finish()
        }
    }
}