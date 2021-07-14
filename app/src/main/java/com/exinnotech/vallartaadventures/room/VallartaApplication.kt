package com.exinnotech.vallartaadventures.room

import android.app.Application
import com.exinnotech.vallartaadventures.room.repository.ReservationRepository

class VallartaApplication: Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { VallartaRoomDatabase.getDatabase(this) }
    val repository by lazy { ReservationRepository(database.reservationDao(), this) }
}