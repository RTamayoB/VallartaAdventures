package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation_table")
class Reservation(
    @PrimaryKey @ColumnInfo(name = "reservation_id") val reservId: Int,
    @ColumnInfo(name = "reservation_detail_id") val reservDetailId: Int,
    @ColumnInfo(name = "guest_name") val guestName : String,
    @ColumnInfo(name = "confirmation_code") val confNum: String,
    @ColumnInfo(name = "agency_name") val agencyName: String,
    @ColumnInfo(name = "hotel_name") val hotelName: String,
    @ColumnInfo(name = "room") val room: String,
    @ColumnInfo(name = "tour_name") val tourName: String,
    @ColumnInfo(name = "idioma") val language: String,
    @ColumnInfo(name = "registration_date") val registrationDate: String,
    @ColumnInfo(name = "email_main_pax") val email: String,
    @ColumnInfo(name = "phone_main_pax") val phone: String,
    @ColumnInfo(name = "total") val amount: Int,
    @ColumnInfo(name = "numCoupon") val coupon: String,
    @ColumnInfo(name = "pickup") val pickup: String,
    @ColumnInfo(name = "montoPickup") val pickupAmount: Int,
    @ColumnInfo(name = "adult_num") val adultNum: Int,
    @ColumnInfo(name = "child_num") val childNum: Int,
    @ColumnInfo(name = "infant_num") val infantNum: Int,
    )