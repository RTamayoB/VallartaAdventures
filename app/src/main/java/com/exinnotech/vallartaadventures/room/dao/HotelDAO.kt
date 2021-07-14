package com.exinnotech.vallartaadventures.room.dao

import androidx.room.*
import com.exinnotech.vallartaadventures.room.entity.Hotel
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDAO {

    @Query("SELECT hotel_name from hotel_table ORDER BY hotel_name ASC")
    fun getHotelNames(): Flow<List<Hotel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotel: Hotel)

    @Delete
    suspend fun delete(hotel: Hotel)

}