package com.exinnotech.vallartaadventures

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.adapter.ReservationAdapter
import com.exinnotech.vallartaadventures.room.VallartaApplication
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.viewmodel.*

class ReservationActivity : AppCompatActivity(), ReservationAdapter.OnItemListener {

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
    lateinit var reservationProgressBar: ProgressBar
    var reservationList = emptyList<Reservation>()
    var adapter: ReservationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        val recyclerView = findViewById<RecyclerView>(R.id.reservation_list)
        reservationProgressBar = findViewById(R.id.reservation_progressBar)
        hotelAuto = findViewById(R.id.hotel_search_auto)
        tourAuto = findViewById(R.id.tour_auto)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getReservations
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        reservationViewModel.getReservations.observe(this) { reservations ->
            // Update the cached copy of the reservations in the adapter.
            reservations.let {
                recyclerView.visibility = View.INVISIBLE
                reservationProgressBar.visibility = View.VISIBLE
                reservationList = emptyList()
                reservationList = it
                adapter = ReservationAdapter(reservationList, this)
                recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                reservationProgressBar.visibility = View.INVISIBLE
            }
        }

        hotelViewModel.getHotelNames.observe(this) { hotels ->
            hotels.let {
                val hotelList = ArrayList<String>()
                hotelList.add("TODOS")
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
                tourList.add("TODOS")
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

        hotelAuto.setOnItemClickListener { adapterView, view, i, l ->

        }


    }

    override fun onItemClick(reservation: Reservation) {
        showReservationDetails(reservation)
    }

    fun showReservationDetails(reservation: Reservation){

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.client_check_in, null)

        val widht : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, widht,height,focusable)

        val guestText = popupView.findViewById<TextView>(R.id.guest_text)
        val confNumText = popupView.findViewById<TextView>(R.id.confirmation_number_text)
        val agencyText = popupView.findViewById<TextView>(R.id.agency_text)
        val hotelText = popupView.findViewById<TextView>(R.id.hotel_text)
        val languageText = popupView.findViewById<TextView>(R.id.language_text)
        val dateText = popupView.findViewById<TextView>(R.id.date_text)
        val emailText = popupView.findViewById<TextView>(R.id.email_text)
        val phoneText = popupView.findViewById<TextView>(R.id.phone_text)
        val amountText = popupView.findViewById<TextView>(R.id.amount_text)
        val doCheckInButton = popupView.findViewById<Button>(R.id.do_check_in_button)
        val undoCheckInButton = popupView.findViewById<Button>(R.id.undo_check_in_button)


        guestText.text = reservation.clientName
        confNumText.text = reservation.confNum
        agencyText.text = reservation.agencyName
        hotelText.text = reservation.hotelName
        languageText.text = reservation.language
        dateText.text = reservation.date.replace("T"," ")
        emailText.text = reservation.email
        phoneText.text = reservation.phone
        amountText.text = reservation.amount.toString()

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

        val queue = Volley.newRequestQueue(this)

        doCheckInButton.setOnClickListener {
            val doCheckInRequest = JsonArrayRequest(Request.Method.POST, "url", null,
                { response ->
                    Log.d("Response", response.toString())
                },
                { error ->
                    Log.d("Error", error.toString())
                })
            doCheckInRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            queue.add(doCheckInRequest)
        }

        undoCheckInButton.setOnClickListener {
            val undoCheckInRequest = JsonArrayRequest(Request.Method.POST, "url", null,
                { response ->
                    Log.d("Response", response.toString())
                },
                { error ->
                    Log.d("Error", error.toString())
                })
            undoCheckInRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            queue.add(undoCheckInRequest)
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