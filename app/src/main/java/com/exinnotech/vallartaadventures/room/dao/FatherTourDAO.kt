package com.exinnotech.vallartaadventures.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exinnotech.vallartaadventures.room.entity.FatherTour
import kotlinx.coroutines.flow.Flow

/**
 * DAO interface responsible of handling communication with the father_tour_table
 */
@Dao
interface FatherTourDAO {

    /**
     * Returns a List containing all objects from the table
     *
     * @return List of FatherTours
     */
    @Query("SELECT * from father_tour_table ORDER BY nombreTourPadre ASC")
    fun getFatherTourNames(): Flow<List<FatherTour>>

    /**
     * Inserts a FatherTour object into the table
     *
     * @param tour FatherTour to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fatherTour: FatherTour)
}