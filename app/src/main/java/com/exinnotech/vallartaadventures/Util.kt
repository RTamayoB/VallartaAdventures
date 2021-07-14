package com.exinnotech.vallartaadventures

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Util() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = dateFormat.format(Date())
    val reservationURL = "http://exinnot.ddns.net:10900/Reservations/GetAll?fecha_reservacion_inicio=${currentDate}T00:00:00&fecha_reservacion_fin=${currentDate}T24:00:00&pickup=true"
    val hotelURL = "http://exinnot.ddns.net:10900/Hotels/GetAllHotelNames"
    val tourURL = "http://exinnot.ddns.net:10900/Tour/GetAll"
}
