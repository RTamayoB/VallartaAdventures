package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.Util
import com.exinnotech.vallartaadventures.room.dao.HotelDAO
import com.exinnotech.vallartaadventures.room.entity.Hotel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repository for Hotel. This is used to manage the way to fetch information, either it be cached
 * from the phone's sql database or getting it from the API
 *
 * @property hotelDAO an instance of the DAO for hotels
 * @param context The context of the activity
 */
class HotelRepository(private val hotelDAO: HotelDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)

    /**
     * Gets the data either locally or from the API
     *
     * @return List of hotel objects
     */
    fun getHotelNames(): Flow<List<Hotel>> {
        checkHotels()
        return hotelDAO.getHotelNames()
    }

    /**
     * Checks if list of hotels exists and returns it, if not it fetches the API
     */
    private fun checkHotels(){
        val listExists = hotelDAO.getHotelNames()
        if(listExists.asLiveData().value.isNullOrEmpty()){
            val jsonArrayRequestHotels = JsonArrayRequest(
                Request.Method.GET, Util("0").hotelURL, null,
                { response ->
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in 0 until response.length()) {
                                val jsonObject = response.getJSONObject(i)
                                val hotel = Hotel(
                                    jsonObject.getString("hotel_name")
                                )
                                hotelDAO.insert(hotel)
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
            queue.add(jsonArrayRequestHotels)
        }
    }

    /**
     * Inserts a hotel into the table
     *
     * @param hotel Hotel to insert
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(hotel: Hotel) {
        hotelDAO.insert(hotel)
    }
}