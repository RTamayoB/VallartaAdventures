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
import com.exinnotech.vallartaadventures.room.dao.TourDAO
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.entity.Tour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TourRepository(private val tourDAO: TourDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)

    fun getTourNames(): Flow<List<Tour>> {
        //TODO: Check if the daily data has been fetched, if not, then delete old data en fetch again
        checkTours()
        // Returns a Flow object directly from the database.
        return tourDAO.getTourNames()
    }

    private fun checkTours(){
        val listExists = tourDAO.getTourNames()
        if(listExists.asLiveData().value.isNullOrEmpty()){
            val jsonArrayRequestTours = JsonArrayRequest(
                Request.Method.GET, Util("0").tourURL, null,
                { response ->
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val tour = Tour(
                                    jsonObject.getInt("idTour"),
                                    jsonObject.getString("nombreTour"),
                                    jsonObject.getString("Descripcion"),
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

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(tour: Tour) {
        tourDAO.insert(tour)
    }
}