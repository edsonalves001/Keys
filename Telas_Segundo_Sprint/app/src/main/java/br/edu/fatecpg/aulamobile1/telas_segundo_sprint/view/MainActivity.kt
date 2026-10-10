package br.edu.fatecpg.aulamobile1.telas_segundo_sprint.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.R
import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.adapter.salasadapter
import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.dao.salaDao
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fabSair = findViewById<FloatingActionButton>(R.id.fab_sair)

        val rvSalas = findViewById<RecyclerView>(R.id.rv_salasDisponiveis)
        rvSalas.adapter = salasadapter(salaDao.buscarSala())
        rvSalas.layoutManager = LinearLayoutManager(this)
        fabSair.setOnClickListener {
            finish()
        }
    }
}