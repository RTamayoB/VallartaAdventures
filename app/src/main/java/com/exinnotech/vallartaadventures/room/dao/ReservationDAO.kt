package com.exinnotech.vallartaadventures.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exinnotech.vallartaadventures.room.entity.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {

    @Query("SELECT reservation_id, name, confirmation_code, agency_name FROM reservation_table")
    fun getReservations(): Flow<List<Reservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation)

    @Query("DELETE FROM reservation_table")
    suspend fun deleteAll()
}