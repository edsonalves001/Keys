package br.edu.fatecpg.appkeys.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import br.edu.fatecpg.appkeys.R;

class efetivarReservaActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_efetivar_reserva)

        val voltar = findViewById<ImageView>(R.id.imgb_voltar);
        val botaoCronograma = findViewById<Button>(R.id.btn_cronograma)
        val botaoPerfil = findViewById<Button>(R.id.btn_perfil)

        botaoPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
        botaoCronograma.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        voltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        return findViewById(R.id.bc_Scanner)
    }
}