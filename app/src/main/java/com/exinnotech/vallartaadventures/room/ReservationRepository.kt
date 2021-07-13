package com.exinnotech.vallartaadventures.room

import androidx.annotation.WorkerThread
import com.exinnotech.vallartaadventures.room.dao.ReservationDAO
import com.exinnotech.vallartaadventures.room.entities.Reservation
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ReservationRepository(private val reservationDAO: ReservationDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val getReservations: Flow<List<Reservation>> = reservationDAO.getReservations()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    //@Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(reservation: Reservation) {
        reservationDAO.insert(reservation)
    }

}