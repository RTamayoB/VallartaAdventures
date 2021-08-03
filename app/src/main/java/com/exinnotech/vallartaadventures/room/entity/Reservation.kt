package com.exinnotech.vallartaadventures.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity object for Reservations. Use this object to edit the reservation_table
 *
 * @property reservId Id for the reservation
 * @property reservDetailId Id for the detail of the reservation
 * @property guestName Name of the guest
 * @property confNum Confirmation Code - We use this to look for matching reservations
 * @property agencyName Name of the agency
 * @property hotelName Name of the hotel
 * @property room Room where the guest is staying
 * @property tourName Name of the tour
 * @property language Language the guest speaks
 * @property registrationDate Date of the registration
 * @property reservationDate Date of the reservation
 * @property checkInDate Date of the check in
 * @property email Email of the guest
 * @property phone Phone of the guest
 * @property amount
 * @property coupon
 * @property pickup Hour to pickup the guest
 * @property pickupAmount
 * @property adultNum Number of adults in the tour
 * @property childNum Number of children in the tour
 * @property infantNum Number of infants in the tour
 * @property vehicleNum Number of vehicles in the tour
 * @property status NOTE: This is the value to check to know if the reservation is board (17) or not (1)
 */
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
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "registration_date") val registrationDate: String,
    @ColumnInfo(name = "reservation_date") val reservationDate: String,
    @ColumnInfo(name = "reservation_time") val reservationTime: String,
    @ColumnInfo(name = "checkin_date") val checkInDate: String,
    @ColumnInfo(name = "email_main_pax") val email: String,
    @ColumnInfo(name = "phone_main_pax") val phone: String,
    @ColumnInfo(name = "total") val amount: Int,
    @ColumnInfo(name = "coupon") val coupon: String,
    @ColumnInfo(name = "pickup") val pickup: String,
    @ColumnInfo(name = "amount_Pickup", defaultValue = "0") val pickupAmount: Int,
    @ColumnInfo(name = "adult_num") val adultNum: Int,
    @ColumnInfo(name = "child_num") val childNum: Int,
    @ColumnInfo(name = "infant_num") val infantNum: Int,
    @ColumnInfo(name = "vehiculo_num") val vehicleNum: Int,
    @ColumnInfo(name = "claveVehiculo") val vehicleCode: String,
    @ColumnInfo(name = "status") var status: Int,
    )