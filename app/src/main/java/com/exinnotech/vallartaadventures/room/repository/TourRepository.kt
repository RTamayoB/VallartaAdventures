package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.room.dao.TourDAO
import com.exinnotech.vallartaadventures.room.entity.Tour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repository for Tour. This is used to manage the way to fetch information, either it be cached
 * from the phone's sql database or getting it from the API
 *
 * @property TourDAO an instance of the DAO for tours
 * @param context The context of the activity
 */
class TourRepository(private val tourDAO: TourDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)
    private val shared = context.getSharedPreferences("searchPreferences",0)
    val login = context.getSharedPreferences("login",0)
    val date = shared.getString("date","0000-00-00")
    val uid = login.getString("uid","NULL")
    val tourURL = "http://exinnot.ddns.net:10900/Reservations/GetTourList?fecha_reservacion_inicio=${date}T00:00:00&fecha_reservacion_fin=${date}T23:59:59&UserId=${uid}"

    /**
     * Gets the data either locally or from the API
     *
     * @return List of tour objects
     */
    fun getTourNames(): Flow<List<Tour>> {
        Log.d("Saving","Tours")
        checkTours()

        return tourDAO.getTourNames()
    }

    /**
     * Checks if list of tours exists and returns it, if not it fetches the API
     */
    private fun checkTours(){
        val listExists = tourDAO.getTourNames()
        if(listExists.asLiveData().value.isNullOrEmpty()){
            val jsonArrayRequestTours = JsonArrayRequest(
                Request.Method.GET, tourURL.replace("\"",""), null,
                { response ->
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val tour = Tour(
                                    jsonObject.getInt("idTour"),
                                    0,
                                    jsonObject.getString("name"),
                                )
                                tourDAO.insert(tour)
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
            jsonArrayRequestTours.retryPolicy = DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            queue.add(jsonArrayRequestTours)
        }
    }

    /**
     * Inserts a tour into the table
     *
     * @param tour Tour to insert
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(tour: Tour) {
        tourDAO.insert(tour)
    }
}