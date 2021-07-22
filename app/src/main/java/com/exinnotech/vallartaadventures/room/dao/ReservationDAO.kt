package com.exinnotech.vallartaadventures.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.exinnotech.vallartaadventures.room.entity.Reservation
import kotlinx.coroutines.flow.Flow

/**
 * DAO interface responsible of handling communication with the reservation_table
 */
@Dao
interface ReservationDAO {

    /**
     * Returns a List containing all objects from the table
     *
     * @return List of Reservations
     */
    @Query("SELECT * FROM reservation_table ORDER BY guest_name ASC")
    fun getReservations(): Flow<List<Reservation>>

    /**
     * Inserts a Reservation object into the table
     *
     * @param reservation Reservation to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation)

    /**
     * Updates the object
     *
     * @param reservation Reservation to update
     */
    @Update
    suspend fun update(reservation: Reservation)

    /**
     * Deletes all data from the table
     * NOTE: Be careful when using this operation
     */
    @Query("DELETE FROM reservation_table")
    suspend fun deleteAll()

    /**
     * Returns a Reservation object with the specified confirmation_code
     *
     * @param confNum confirmation code to match
     * @return Matching reservation
     */
    @Query("SELECT * FROM reservation_table WHERE confirmation_code=:confNum")
    fun getReservationById(confNum: String): LiveData<Reservation>
}