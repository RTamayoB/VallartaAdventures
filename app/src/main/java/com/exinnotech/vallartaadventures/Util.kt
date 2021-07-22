package com.exinnotech.vallartaadventures

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Util(val date: String, val userId: String) {
    //TODO: Change way to pass in date and uid
    val testURL = "http://exinnot.ddns.net:10900/Reservations/GetAllByTourDate?fecha_reservacion_inicio=2021-06-15T00:00:00&fecha_reservacion_fin=2021-06-15T23:59:59&pickup=true"
    val reservationURL = "http://exinnot.ddns.net:10900/Reservations/GetAllByTourDate?fecha_reservacion_inicio=${date}&fecha_reservacion_fin=${date}&UserId=${userId}&pickup=true"
    val hotelURL = "http://exinnot.ddns.net:10900/Hotels/GetAllHotelNames"
    val tourURL = "http://exinnot.ddns.net:10900/Tour/GetAll"
}
