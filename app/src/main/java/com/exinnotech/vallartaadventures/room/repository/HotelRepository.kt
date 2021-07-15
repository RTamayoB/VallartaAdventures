package com.exinnotech.vallartaadventures.room.repository

import android.content.Context
import android.content.SharedPreferences
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

class HotelRepository(private val hotelDAO: HotelDAO, context: Context) {
    private val queue = Volley.newRequestQueue(context)

    fun getHotelNames(): Flow<List<Hotel>> {
        //TODO: Check if the daily data has been fetched, if not, then delete old data en fetch again
        checkHotels()
        return hotelDAO.getHotelNames()
    }

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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(hotel: Hotel) {
        hotelDAO.insert(hotel)
    }
}