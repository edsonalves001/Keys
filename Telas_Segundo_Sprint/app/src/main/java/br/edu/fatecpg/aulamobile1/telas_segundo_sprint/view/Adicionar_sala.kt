package br.edu.fatecpg.aulamobile1.telas_segundo_sprint.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.R
import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.dao.salaDao
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Adicionar_sala : AppCompatActivity(R.layout.activity_adicionar_sala) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nomesala = findViewById<EditText>(R.id.edt_sala)
        val botao = findViewById<Button>(R.id.btn_salvar)
        val fab = findViewById<FloatingActionButton>(R.id.fab_salas)

        botao.setOnClickListener {
            val nome = nomesala.text.toString()
            Toast.makeText(this, salaDao.salvarSala(nome), Toast.LENGTH_SHORT).show()
            nomesala.text.clear()
        }

        fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}