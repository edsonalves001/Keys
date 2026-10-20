package br.edu.fatecpg.appkeys.views

import android.os.Bundle
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import br.edu.fatecpg.appkeys.R;

class efetivarReservaActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_efetivar_reserva)

        return findViewById(R.id.bc_Scanner)
    }
}