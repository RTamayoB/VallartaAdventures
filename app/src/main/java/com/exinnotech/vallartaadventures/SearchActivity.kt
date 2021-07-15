package com.exinnotech.vallartaadventures

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.exinnotech.vallartaadventures.scanning.CaptureActivityPortrait
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var goToList: TextView
    private lateinit var searchQRButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        goToList = findViewById(R.id.go_list_txt)
        searchQRButton = findViewById(R.id.search_qr_button)

        goToList.setOnClickListener {
            goToList.setTextColor(resources.getColor(R.color.vallarta_blue))
            val goToReservation = Intent(this, ReservationActivity::class.java)
            startActivity(goToReservation)
        }

        searchQRButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.captureActivity = CaptureActivityPortrait::class.java
            integrator.setOrientationLocked(true)
            integrator.setPrompt("Escanee código de Confirmación")
            integrator.initiateScan()
        }

    }

    override fun onResume() {
        super.onResume()
        goToList.setTextColor(Color.LTGRAY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode, data)
        if(result.contents != null){
            Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}