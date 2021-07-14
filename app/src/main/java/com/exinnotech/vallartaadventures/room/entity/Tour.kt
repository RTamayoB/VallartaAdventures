package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tour_table")
class Tour(
    @PrimaryKey @ColumnInfo(name = "idTour") val tourId: Int,
    @ColumnInfo(name = "nombreTour") val tourName: String,
    @ColumnInfo(name = "Descripcion") val description: String
)