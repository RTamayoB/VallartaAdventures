package com.exinnotech.vallartaadventures.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation_table")
class Reservation(
    @PrimaryKey @ColumnInfo(name = "reservation_id") val reservId: Int,
    @ColumnInfo(name = "confirmation_code") val confNum: String,
    @ColumnInfo(name = "agency_name") val agencyName: String)