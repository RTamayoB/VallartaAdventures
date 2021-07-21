package com.exinnotech.vallartaadventures.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.Util
import com.exinnotech.vallartaadventures.data.model.LoggedInUser
import com.exinnotech.vallartaadventures.room.entity.Tour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(context: Context) {

    val queue = Volley.newRequestQueue(context)

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val user = LoggedInUser("UID", username)
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}