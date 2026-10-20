package br.edu.fatecpg.appkeys.views

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.fatecpg.appkeys.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botaoQR = findViewById<Button>(R.id.btn_qr_scan)

        botaoQR.setOnClickListener {
            val options = ScanOptions()
            options.setBeepEnabled(true) //Faz com que após a leitura do codigo qr, saia um beep de confirmação
            options.setOrientationLocked(true) //Mantém a posição da camera fixa
            options.setCaptureActivity(efetivarReservaActivity::class.java)//Diz qual activity vai abrir o leitor

            barLauncher.launch(options)
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