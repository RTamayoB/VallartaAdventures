package com.exinnotech.vallartaadventures.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class APIConnection(val context: Context) {

    val queue = Volley.newRequestQueue(context)

    val hotelURL = "http://exinnot.ddns.net:10900/Hotels/GetAllHotelNames"
    val jsonArrayRequestHotels = JsonArrayRequest(Request.Method.GET, hotelURL, null,
        { response ->
           Log.d("Response", response.toString())
        },
        { error ->
            Log.d("Error", error.toString())
        }
    )
}