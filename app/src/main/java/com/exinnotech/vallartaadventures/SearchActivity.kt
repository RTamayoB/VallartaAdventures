package com.exinnotech.vallartaadventures

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.exinnotech.vallartaadventures.room.VallartaApplication
import com.exinnotech.vallartaadventures.room.viewmodel.ReservationViewModel
import com.exinnotech.vallartaadventures.room.viewmodel.ReservationViewModelFactory
import com.exinnotech.vallartaadventures.scanning.CaptureActivityPortrait
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class SearchActivity : AppCompatActivity() {

    private val reservationViewModel: ReservationViewModel by viewModels {
        ReservationViewModelFactory((application as VallartaApplication).reservationRepository)
    }

    private lateinit var goToList: TextView
    private lateinit var couponEdit: EditText
    private lateinit var searchQRButton: Button
    private lateinit var searchCouponButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        goToList = findViewById(R.id.go_list_txt)
        couponEdit = findViewById(R.id.coupon_edt)
        searchQRButton = findViewById(R.id.search_qr_button)
        searchCouponButton = findViewById<Button>(R.id.search_coupon_button)

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

        searchCouponButton.setOnClickListener {
            if(couponEdit.text.isNullOrEmpty()){
                Toast.makeText(this, "Ingrese un cúpon o código de confirmación", Toast.LENGTH_LONG).show()
            }else{
                reservationViewModel.getReservationsByConfNum(couponEdit.text.toString()).observe(this) { reservation ->
                    reservation.let {
                        if(it == null){
                            Toast.makeText(this, "No se encontro una reservación con este código", Toast.LENGTH_LONG).show()
                        }else{
                            val checkInPopupWindow = CheckInActivity(this,findViewById(android.R.id.content), it)
                            checkInPopupWindow.showCheckInPopup()
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        goToList.setTextColor(Color.LTGRAY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode, data)
        if(result.contents != null){
            reservationViewModel.getReservationsByConfNum(result.contents).observe(this) { reservation ->
                reservation.let {
                    if(it == null){
                        Toast.makeText(this, "No se encontro una reservación con este código", Toast.LENGTH_LONG).show()
                    }else{
                        val checkInPopupWindow = CheckInActivity(this,findViewById(android.R.id.content), it)
                        checkInPopupWindow.showCheckInPopup()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}