package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity object for Tours. Use this object to edit the tours_table
 *
 * @property tourId Id of the tour
 * @property tourName Name of the Tour
 * @property description Description for the tour
 */
@Entity(tableName = "tour_table")
class Tour(
    @PrimaryKey @ColumnInfo(name = "idTour") val tourId: Int,
    @ColumnInfo(name = "idTourPadre") val fatherTourId: Int,
    @ColumnInfo(name = "name") val name: String,
)