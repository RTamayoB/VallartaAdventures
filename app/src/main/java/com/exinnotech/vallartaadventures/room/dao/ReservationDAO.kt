package com.exinnotech.vallartaadventures.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exinnotech.vallartaadventures.room.entity.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {

    @Query("SELECT * FROM reservation_table ORDER BY guest_name ASC")
    fun getReservations(): Flow<List<Reservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation)

    @Query("DELETE FROM reservation_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM reservation_table WHERE confirmation_code=:confNum")
    fun getReservationById(confNum: String): LiveData<Reservation>
}