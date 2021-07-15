package com.exinnotech.vallartaadventures

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

class Util(val date: String) {
    val reservationURL = "http://exinnot.ddns.net:10900/Reservations/GetAll?fecha_reservacion_inicio=${date}T00:00:00&fecha_reservacion_fin=${date}T24:00:00&pickup=true"
    val hotelURL = "http://exinnot.ddns.net:10900/Hotels/GetAllHotelNames"
    val tourURL = "http://exinnot.ddns.net:10900/Tour/GetAll"
}
