package com.exinnotech.vallartaadventures.room.dao

import androidx.room.*
import com.exinnotech.vallartaadventures.room.entity.Hotel
import kotlinx.coroutines.flow.Flow

/**
 * DAO interface responsible of handling communication with the hotel_table
 */
@Dao
interface HotelDAO {

    /**
     * Returns a List containing all objects from the table
     *
     * @return List of Hotels
     */
    @Query("SELECT hotel_name from hotel_table ORDER BY hotel_name ASC")
    fun getHotelNames(): Flow<List<Hotel>>

    /**
     * Inserts a Hotel object into the table
     *
     * @param hotel Hotel to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotel: Hotel)

    /**
     * Deletes a Hotel object from the table
     *
     * @param hotel Hotel to delete
     */
    @Delete
    suspend fun delete(hotel: Hotel)

}