package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity object for FatherTours. Use this object to edit the father_tour_table
 *
 * @property fatherTourId Id of the FatherTour
 * @property name Name of the FatherTour
 */
@Entity(tableName = "father_tour_table")
class FatherTour (
    @PrimaryKey @ColumnInfo(name = "idTourPadre") val fatherTourId: Int,
    @ColumnInfo(name = "nombreTourPadre") val name: String,
)
