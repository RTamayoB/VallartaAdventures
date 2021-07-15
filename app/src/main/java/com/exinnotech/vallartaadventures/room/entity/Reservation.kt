package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation_table")
class Reservation(
    @PrimaryKey @ColumnInfo(name = "reservation_id") val reservId: Int,
    @ColumnInfo(name = "name") val clientName : String,
    @ColumnInfo(name = "confirmation_code") val confNum: String,
    @ColumnInfo(name = "agency_name") val agencyName: String,
    @ColumnInfo(name = "hotel_name") val hotelName: String,
    @ColumnInfo(name = "idioma") val language: String,
    @ColumnInfo(name = "registration_date") val date: String,
    @ColumnInfo(name = "email_main_pax") val email: String,
    @ColumnInfo(name = "phone_main_pax") val phone: String,
    @ColumnInfo(name = "total") val amount: Int,
    )