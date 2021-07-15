package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.Util
import com.exinnotech.vallartaadventures.room.dao.ReservationDAO
import com.exinnotech.vallartaadventures.room.entity.Reservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ReservationRepository(private val reservationDAO: ReservationDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)
    val shared = context.getSharedPreferences("searchPreferences",0)
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    //val getReservations: Flow<List<Reservation>> = reservationDAO.getReservations()

    //Example of retrieval
    fun getReservations() : Flow<List<Reservation>> {
        //Check if data has already been fetched
        //TODO: Check if the daily data has been fetched, if not, then delete old data en fetch again
        checkReservations()
       // Returns a Flow object directly from the database.
       return reservationDAO.getReservations()
   }


    private fun checkReservations(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.US)
        val currentDate = dateFormat.format(Date())
        val storedDate = shared.getString("date","0000-00-00")
        Log.d("Current", currentDate)
        Log.d("Stored",storedDate!!)
        if(storedDate != currentDate){
            shared.edit().putString("date", currentDate).apply()
            Log.d("Current Time",Util(currentDate).reservationURL)
            val listExists = reservationDAO.getReservations()
            if(listExists.asLiveData().value.isNullOrEmpty()){
                val jsonArrayRequestReservations = JsonArrayRequest(
                    Request.Method.GET, Util(currentDate).reservationURL, null,
                    { response ->
                        try {
                            CoroutineScope(Dispatchers.IO).launch {
                                for (i in 0 until response.length()) {
                                    val jsonObject = response.getJSONObject(i)
                                    val reservation = Reservation(
                                        jsonObject.getInt("reservation_id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("confirmation_code"),
                                        jsonObject.getString("agency_name"),
                                        jsonObject.getString("hotel_name"),
                                        jsonObject.getString("idioma"),
                                        jsonObject.getString("registration_date"),
                                        jsonObject.getString("email_main_pax"),
                                        jsonObject.getString("phone_main_pax"),
                                        jsonObject.getInt("total")
                                    )
                                    reservationDAO.insert(reservation)
                                }
                            }
                        }catch (e: Exception){
                            Log.d("Error handling data", e.toString())
                        }
                    },
                    { error ->
                        Log.d("Error", error.toString())
                    }
                )
                jsonArrayRequestReservations.retryPolicy = DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                queue.add(jsonArrayRequestReservations)
            }
        }
        else{
            Log.d("Update","Not required")
        }
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(reservation: Reservation) {
        reservationDAO.insert(reservation)
    }

}