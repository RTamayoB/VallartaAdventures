package com.exinnotech.vallartaadventures

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.room.VallartaApplication
import com.exinnotech.vallartaadventures.room.viewmodel.*
import com.google.android.material.textfield.TextInputLayout

class ReservationActivity : AppCompatActivity() {

    private val reservationViewModel: ReservationViewModel by viewModels {
        ReservationViewModelFactory((application as VallartaApplication).reservationRepository)
    }

    private val hotelViewModel: HotelViewModel by viewModels {
        HotelViewModelFactory((application as VallartaApplication).hotelRepository)
    }

    private val tourViewModel: TourViewModel by viewModels {
        TourViewModelFactory((application as VallartaApplication).tourRepository)
    }

    lateinit var hotelAuto: AutoCompleteTextView
    lateinit var tourAuto: AutoCompleteTextView
    var adapter: ReservationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        //TODO: Create the Classes for the Tours and Hotels

        val recyclerView = findViewById<RecyclerView>(R.id.reservation_list)
        hotelAuto = findViewById(R.id.hotel_search_auto)
        tourAuto = findViewById(R.id.tour_auto)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getReservations
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        reservationViewModel.getReservations.observe(this) { reservations ->
            // Update the cached copy of the reservations in the adapter.
            reservations.let {
                adapter = ReservationAdapter(it)
                recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }

        hotelViewModel.getHotelNames.observe(this) { hotels ->
            hotels.let {
                val hotelList = ArrayList<String>()
                for(hotel in it){
                    hotelList.add(hotel.name)
                }
                val hotelAdapter =ArrayAdapter(this, android.R.layout.simple_list_item_1, hotelList)
                hotelAuto.setAdapter(hotelAdapter)
            }
        }

        tourViewModel.getTourNames.observe(this) { tours ->
            tours.let {
                val tourList = ArrayList<String>()
                for(tour in it){
                    tourList.add(tour.tourName)
                }
                val tourAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tourList)
                tourAuto.setAdapter(tourAdapter)
            }
        }

        hotelAuto.setOnClickListener {
            hotelAuto.showDropDown()
        }

        tourAuto.setOnClickListener {
            tourAuto.showDropDown()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d("onQueryTextChange", "query: $p0")
                adapter?.filter?.filter(p0)
                return true
            }

        })

        return true
    }
}