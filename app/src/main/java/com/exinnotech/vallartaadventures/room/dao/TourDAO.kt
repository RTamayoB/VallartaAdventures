package com.exinnotech.vallartaadventures.room.dao

import androidx.room.*
import com.exinnotech.vallartaadventures.room.entity.Tour
import kotlinx.coroutines.flow.Flow

@Dao
interface TourDAO {

    @Query("SELECT idTour, nombreTour, Descripcion FROM tour_table ORDER BY nombreTour ASC")
    fun getTourNames(): Flow<List<Tour>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tour: Tour)
}