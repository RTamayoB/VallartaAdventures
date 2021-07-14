package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
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

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ReservationRepository(private val reservationDAO: ReservationDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    //val getReservations: Flow<List<Reservation>> = reservationDAO.getReservations()

    //Example of retrieval
    fun getReservations() : Flow<List<Reservation>> {
        //Check if data has already been fetched
        checkReservations()
       // Returns a Flow object directly from the database.
       return reservationDAO.getReservations()
   }


    private fun checkReservations(){
        val listExists = reservationDAO.getReservations()
        if(listExists.asLiveData().value.isNullOrEmpty()){
            val jsonArrayRequestReservations = JsonArrayRequest(
                Request.Method.GET, Util().reservationURL, null,
                { response ->
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val reservation = Reservation(
                                    jsonObject.getInt("reservation_id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("confirmation_code"),
                                    jsonObject.getString("agency_name")
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
            queue.add(jsonArrayRequestReservations)
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