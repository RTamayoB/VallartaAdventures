package com.exinnotech.vallartaadventures.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exinnotech.vallartaadventures.room.entities.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {

    @Query("SELECT confirmation_code, agency_name FROM reservation_table")
    fun getReservations(): Flow<List<Reservation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reservation: Reservation)
}