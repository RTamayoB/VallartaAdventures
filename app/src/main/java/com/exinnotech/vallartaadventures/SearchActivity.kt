package com.exinnotech.vallartaadventures

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView

class SearchActivity : AppCompatActivity() {

    private lateinit var goToList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        goToList = findViewById(R.id.go_list_txt)

        goToList.setOnClickListener {
            goToList.setTextColor(resources.getColor(R.color.vallarta_blue))
            val goToReservation = Intent(this, ReservationActivity::class.java)
            startActivity(goToReservation)
        }

    }

    override fun onResume() {
        super.onResume()
        goToList.setTextColor(Color.LTGRAY)
    }
}