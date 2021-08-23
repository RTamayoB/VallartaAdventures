package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.room.dao.ReservationDAO
import com.exinnotech.vallartaadventures.room.entity.Reservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for Reservation. This is used to manage the way to fetch information, either it be cached
 * from the phone's sql database or getting it from the API
 *
 * @property ReservationDAO an instance of the DAO for reservations
 * @param context The context of the activity
 */
class ReservationRepository(private val reservationDAO: ReservationDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)
    private val shared = context.getSharedPreferences("searchPreferences",0)
    val login = context.getSharedPreferences("login",0)!!

    /**
     * Gets the data either locally or from the API
     *
     * @return List of reservation objects
     */
    fun getReservations() : Flow<List<Reservation>> {
        CoroutineScope(Dispatchers.IO).launch {
            deleteAll()
        }
        checkReservations()
       return reservationDAO.getReservations()
   }

    /**
     * Checks if list of reservations from the current date exists and returns it, if not it fetches the API
     */
    private fun checkReservations(){
        Log.d("ReservationRepository", "Fetching...")
        val uid = login.getString("uid","NULL")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.US)
        val currentDate = dateFormat.format(Date())
        val storedDate = shared.getString("date","0000-00-00")
        if(storedDate != currentDate) {
            shared.edit().putString("date", currentDate).apply()
        }
        val reservationURL = "http://exinnot.ddns.net:10900/Reservations/GetAllByTourDate?fecha_reservacion_inicio=${currentDate}T00:00:00&fecha_reservacion_fin=${currentDate}T23:59:59&UserId=$uid&pickup=true"
        Log.d("ReservationRepository","URL: $reservationURL")
        //val listExists = reservationDAO.getReservations()
        //if(listExists.asLiveData().value.isNullOrEmpty()){
        val jsonArrayRequestReservations = JsonArrayRequest(
            Request.Method.GET, reservationURL.replace("\"",""), null,
            { response ->
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in 0 until response.length()) {
                            val jsonReservation = response.getJSONObject(i)
                            val reservation = Reservation(
                                jsonReservation.getInt("reservation_id"),
                                jsonReservation.getInt("reservation_detail_id"),
                                jsonReservation.getString("guest_name"),
                                jsonReservation.getString("confirmation_code"),
                                jsonReservation.getString("agency_name"),
                                jsonReservation.getString("hotel_name"),
                                jsonReservation.getString("room"),
                                jsonReservation.getString("tour_name"),
                                jsonReservation.getString("language"),
                                jsonReservation.getString("registration_date"),
                                jsonReservation.getString("reservation_date"),
                                jsonReservation.getString("reservation_time"),
                                jsonReservation.getString("checkin_date"),
                                jsonReservation.getString("email_main_pax"),
                                jsonReservation.getString("phone_main_pax"),
                                jsonReservation.getInt("total"),
                                jsonReservation.getString("coupon"),
                                jsonReservation.getString("pickup"),
                                0,
                                jsonReservation.getInt("adult_num"),
                                jsonReservation.getInt("child_num"),
                                jsonReservation.getInt("infant_num"),
                                jsonReservation.getInt("vehiculo_num"),
                                jsonReservation.getString("claveVehiculo"),
                                jsonReservation.getInt("status"),
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
        /*
            }
        else{
            Log.d("Update","Not required")
        }*/
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    /**
     * Inserts a reservation into the table
     *
     * @param reservation Reservation to insert
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(reservation: Reservation) {
        reservationDAO.insert(reservation)
    }

    /**
     * Uses the DAO to update the item
     *
     * @param reservation Reservation to update
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(reservation: Reservation) {
        reservationDAO.update(reservation)
    }

    /**
     * Returns the reservation with matching confirmation_code
     *
     * @param confNum confirmation_code to look for
     * @return Matching reservation
     */
    @WorkerThread
    fun getReservationByConfNum(confNum: String): LiveData<Reservation> {
        return reservationDAO.getReservationById(confNum)
    }

    @WorkerThread
    suspend fun deleteAll(){
        reservationDAO.deleteAll()
    }

}