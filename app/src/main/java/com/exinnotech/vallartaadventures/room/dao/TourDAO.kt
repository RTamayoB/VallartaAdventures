package com.exinnotech.vallartaadventures.room.dao

import androidx.room.*
import com.exinnotech.vallartaadventures.room.entity.Tour
import kotlinx.coroutines.flow.Flow

/**
 * DAO interface responsible of handling communication with the tour_table
 */
@Dao
interface TourDAO {

    /**
     * Returns a List containing all objects from the table
     *
     * @return List of Tours
     */
    @Query("SELECT idTour, nombreTour, Descripcion FROM tour_table ORDER BY nombreTour ASC")
    fun getTourNames(): Flow<List<Tour>>

    /**
     * Inserts a Tour object into the table
     *
     * @param tour Tour to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tour: Tour)
}