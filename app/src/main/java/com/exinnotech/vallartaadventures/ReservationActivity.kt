package com.exinnotech.vallartaadventures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.room.ReservationViewModel
import com.exinnotech.vallartaadventures.room.ReservationViewModelFactory
import com.exinnotech.vallartaadventures.room.VallartaApplication

class ReservationActivity : AppCompatActivity() {

    /*
    private val reservationViewModel: ReservationViewModel.kt by reservationViewModel {
        ReservationViewModelFactory((application as VallartaApplication).repository)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        val recyclerView = findViewById<RecyclerView>(R.id.reservation_list)
        val adapter = ReservationAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getReservations
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        /*reservationViewModel.getReservations.observe(owner = this) { reservations ->
            // Update the cached copy of the reservations in the adapter.
            reservations.let { adapter. }
        }*/


    }
}