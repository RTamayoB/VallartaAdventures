package com.exinnotech.vallartaadventures

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Util(val date: String) {
    val testURL = "http://exinnot.ddns.net:10900/Reservations/GetAllByRegistrationDate?from_date=2021-06-11T00%3A00%3A00&end_date=2021-06-11T23%3A59%3A59&pickup=true"
    val newReservURL = "http://exinnot.ddns.net:10900/Reservations/GetAllByRegistrationDate?from_date=${date}T00:00:00&end_date=${date}T23:59:59&pickup=true"
    val reservationURL = "http://exinnot.ddns.net:10900/Reservations/GetAll?fecha_reservacion_inicio=${date}T00:00:00&fecha_reservacion_fin=${date}T24:00:00&pickup=true"
    val hotelURL = "http://exinnot.ddns.net:10900/Hotels/GetAllHotelNames"
    val tourURL = "http://exinnot.ddns.net:10900/Tour/GetAll"
}
