package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity object for Hotels. Use this object to edit the hotel_table
 *
 * @property name Name of the hotel
 */
@Entity(tableName = "hotel_table")
class Hotel(
    @PrimaryKey @ColumnInfo(name = "hotel_name") val name: String
)
