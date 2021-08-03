package com.exinnotech.vallartaadventures

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.adapter.ReservationAdapter
import com.exinnotech.vallartaadventures.databinding.ActivityReservationBinding
import com.exinnotech.vallartaadventures.room.VallartaApplication
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.viewmodel.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial

//TODO: Add documentation
//TODO: Make it so the filter does the relation between father and child tours
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

    private val fatherTourViewModel: FatherTourViewModel by viewModels {
        FatherTourViewModelFactory((application as VallartaApplication).fatherTourRepository)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var hotelAuto: AutoCompleteTextView
    lateinit var hotelZoneAuto: AutoCompleteTextView
    lateinit var tourAuto: AutoCompleteTextView
    lateinit var reservationProgressBar: ProgressBar
    lateinit var boardSwitch: SwitchMaterial
    lateinit var filterGroup: ChipGroup
    lateinit var nameChip: Chip
    lateinit var confirmationChip: Chip
    lateinit var hotelZoneChip: Chip
    lateinit var hotelChip: Chip
    lateinit var tourChip: Chip
    var reservationList = emptyList<Reservation>()
    var adapter: ReservationAdapter? = null

    private lateinit var binding: ActivityReservationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.reservationList
        reservationProgressBar = binding.reservationProgressBar
        hotelAuto = binding.hotelSearchAuto
        hotelZoneAuto = binding.hotelZoneAuto
        tourAuto = binding.tourAuto
        boardSwitch = binding.boardSwitch
        filterGroup = binding.filterGroup
        nameChip = binding.nameChip
        confirmationChip = binding.confNumChip

        recyclerView.layoutManager = LinearLayoutManager(this)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fillHourSpinner())
        binding.hourFilter?.adapter = spinnerAdapter

        // Add an observer on the LiveData returned by getReservations
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        reservationViewModel.getReservations.observe(this) { reservations ->
            // Update the cached copy of the reservations in the adapter.
            reservations.let {
                Log.d("Loading", "Reservations")
                reservationProgressBar.visibility = View.INVISIBLE
                reservationList = emptyList()
                reservationList = it
                Log.d("ReservationList", reservationList.toString())
                adapter = ReservationAdapter(reservationList, this)
                recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }

        hotelViewModel.getHotelNames.observe(this) { hotels ->
            hotels.let {
                val hotelList = ArrayList<String>()
                hotelList.add("TODOS")
                for (hotel in it) {
                    hotelList.add(hotel.name)
                }
                val hotelAdapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, hotelList)
                hotelAuto.setAdapter(hotelAdapter)
            }
        }

        fatherTourViewModel.getFatherTourNames.observe(this) { fatherTours ->
            fatherTours.let {
                val tourList = ArrayList<String>()
                tourList.add("TODOS")
                for (tour in it) {
                    tourList.add(tour.name)
                }
                val tourAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tourList)
                tourAuto.setAdapter(tourAdapter)
                tourViewModel.getTourNames
            }
        }

        hotelAuto.setText("TODOS")
        hotelZoneAuto.setText("TODOS")
        tourAuto.setText("TODOS")

        hotelAuto.setOnClickListener {
            hotelAuto.showDropDown()
        }

        tourAuto.setOnClickListener {
            tourAuto.showDropDown()
        }

        hotelAuto.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("Selected", i.toString())
            var to = ""
            to = if(binding.hourFilter.selectedItem.toString() != "23:00:00"){
                binding.hourFilter.getItemAtPosition(binding.hourFilter.selectedItemPosition+1).toString()
            } else{
                "24:00:00"
            }
            adapter?.filterWithoutQuery(
                "TODOS",
                hotelAuto.text.toString(),
                tourAuto.text.toString(),
                binding.hourFilter.selectedItem.toString()+"-"+to,
                boardSwitch.isChecked
            )
        }


        tourAuto.setOnItemClickListener { adapterView, view, i, l ->
            var to = ""
            to = if(binding.hourFilter.selectedItem.toString() != "23:00:00"){
                binding.hourFilter.getItemAtPosition(binding.hourFilter.selectedItemPosition+1).toString()
            } else{
                "24:00:00"
            }
            adapter?.filterWithoutQuery(
                "TODOS",
                hotelAuto.text.toString(),
                tourAuto.text.toString(),
                binding.hourFilter.selectedItem.toString()+"-"+to,
                boardSwitch.isChecked
            )
        }

        binding.hourFilter?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("Selected", binding.hourFilter.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        boardSwitch.setOnCheckedChangeListener { compoundButton, b ->
            var to = ""
            to = if(binding.hourFilter.selectedItem.toString() != "23:00:00"){
                binding.hourFilter.getItemAtPosition(binding.hourFilter.selectedItemPosition+1).toString()
            } else{
                "24:00:00"
            }
            adapter?.filterWithoutQuery(
                "TODOS",
                hotelAuto.text.toString(),
                tourAuto.text.toString(),
                binding.hourFilter.selectedItem.toString()+"-"+to,
                b
            )
        }

        binding.reloadListFab.setOnClickListener {
            reservationProgressBar.visibility = View.VISIBLE
            getReservations()
        }

    }

    override fun onItemClick(reservation: Reservation) {
        val checkInPopupWindow = CheckInActivity(this, reservation, reservationViewModel)
        checkInPopupWindow.showCheckInPopup()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                filterGroup.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                var to = ""
                to = if(binding.hourFilter.selectedItem.toString() != "23:00:00"){
                    binding.hourFilter.getItemAtPosition(binding.hourFilter.selectedItemPosition+1).toString()
                } else{
                    "24:00:00"
                }
                adapter?.myFilter("",
                    nameChecked = false,
                    confNumChecked = false,
                    hotelZoneChecked = false,
                    hotelChecked = false,
                    binding.hourFilter.selectedItem.toString()+"-"+to,
                    tourChecked = false
                )
                filterGroup.visibility = View.GONE
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d("onQueryTextChange", "query: $p0")
                //adapter?.filter(p0!!, "TODOS",hotelAuto.text.toString(), tourAuto.text.toString(), boardSwitch.isChecked)
                var to = ""
                to = if(binding.hourFilter.selectedItem.toString() != "23:00:00"){
                    binding.hourFilter.getItemAtPosition(binding.hourFilter.selectedItemPosition+1).toString()
                } else{
                    "24:00:00"
                }
                adapter?.myFilter(p0!!,nameChip.isChecked, confirmationChip.isChecked, hotelZoneChip.isChecked, hotelChip.isChecked, binding.hourFilter.selectedItem.toString()+"-"+to, tourChip.isChecked)
                return true
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("Pressed",item.itemId.toString())
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    fun fillHourSpinner(): ArrayList<String> {
        val spinnerArray = arrayListOf<String>()
        for(i in 0 until 10){
            spinnerArray.add("0$i:00:00")
        }
        for(j in 10 until 24){
            spinnerArray.add("$j:00:00")
        }
        return spinnerArray
    }

    fun getReservations(){
        reservationViewModel.getReservations.observe(this) { reservations ->
            // Update the cached copy of the reservations in the adapter.
            reservations.let {
                Log.d("Loading", "Reservations")
                reservationProgressBar.visibility = View.INVISIBLE
                reservationList = emptyList()
                reservationList = it
                Log.d("ReservationList", reservationList.toString())
                adapter = ReservationAdapter(reservationList, this)
                recyclerView.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }

    }
}