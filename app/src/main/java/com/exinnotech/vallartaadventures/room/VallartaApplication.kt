package com.exinnotech.vallartaadventures.room

import android.app.Application
import com.exinnotech.vallartaadventures.room.repository.HotelRepository
import com.exinnotech.vallartaadventures.room.repository.ReservationRepository
import com.exinnotech.vallartaadventures.room.repository.TourRepository

/**
 * Instance of our app to instantiate certain components
 */
class VallartaApplication: Application() {
    // Using by lazy so the database and the repositories are only created when they're needed
    // rather than when the application starts
    val database by lazy { VallartaRoomDatabase.getDatabase(this) }
    val reservationRepository by lazy { ReservationRepository(database.reservationDao(), this) }
    val hotelRepository by lazy { HotelRepository(database.hotelDao(), this) }
    val tourRepository by lazy { TourRepository(database.tourDao(), this) }
}