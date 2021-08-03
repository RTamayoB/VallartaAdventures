package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.room.dao.FatherTourDAO
import com.exinnotech.vallartaadventures.room.entity.FatherTour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repository for FatherTour. This is used to manage the way to fetch information, either it be cached
 * from the phone's sql database or getting it from the API
 *
 * @property FatherTourDAO an instance of the DAO for father tours
 * @param context The context of the activity
 */
class FatherTourRepository(private val fatherTourDAO: FatherTourDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)
    private val shared = context.getSharedPreferences("searchPreferences",0)
    val login = context.getSharedPreferences("login",0)
    val date = shared.getString("date","0000-00-00")
    val uid = login.getString("uid","NULL")
    val fatherTourURL = "http://exinnot.ddns.net:10900/Reservations/GetTourList?fecha_reservacion_inicio=${date}T00:00:00&fecha_reservacion_fin=${date}T23:59:59&UserId=$uid"

    /**
     * Gets the data either locally or from the API
     *
     * @return List of father tour objects
     */
    fun getFatherTourNames(): Flow<List<FatherTour>> {
        checkFatherTours()

        return fatherTourDAO.getFatherTourNames()
    }

    /**
     * Checks if list of father tours exists and returns it, if not it fetches the API
     */
    private fun checkFatherTours(){
        val listExists = fatherTourDAO.getFatherTourNames()
        if(listExists.asLiveData().value.isNullOrEmpty()){
            val jsonArrayRequestTours = JsonArrayRequest(
                Request.Method.GET, fatherTourURL.replace("\"",""), null,
                { response ->
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val tour = FatherTour(
                                    jsonObject.getInt("idTourPadre"),
                                    jsonObject.getString("nombreTourPadre"),
                                )
                                fatherTourDAO.insert(tour)
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
     * Inserts a father tour into the table
     *
     * @param fatherTour FatherTour to insert
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(fatherTour: FatherTour) {
        fatherTourDAO.insert(fatherTour)
    }

}