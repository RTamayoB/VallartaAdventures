package com.exinnotech.vallartaadventures

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import com.github.anastaciocintra.escpos.barcode.BarCode
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*

var bluetoothAdapter: BluetoothAdapter? = null
var bluetoothSocket: BluetoothSocket? = null
var bluetoothDevice: BluetoothDevice? = null

var outputStream: OutputStream? = null
var inputStream: InputStream? = null
var thread: Thread? = null

var readBuffer: ByteArray = byteArrayOf()
var readBufferPosition = 0

@Volatile
var stopWorker = false

class CheckInActivity(val activity: Activity, val location: View, val reservation: Reservation) {

    val queue = Volley.newRequestQueue(activity.applicationContext)


    fun showCheckInPopup() {
        val inflater = activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.client_check_in, null)

        val widht : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, widht,height,focusable)

        val tourText = popupView.findViewById<TextView>(R.id.tour_text)
        val confNumText = popupView.findViewById<TextView>(R.id.confirmation_number_text)
        val couponText = popupView.findViewById<TextView>(R.id.coupon_text)
        val guestText = popupView.findViewById<TextView>(R.id.guest_text)
        val emailText = popupView.findViewById<TextView>(R.id.email_text)
        val phoneText = popupView.findViewById<TextView>(R.id.phone_text)
        val adultsText = popupView.findViewById<TextView>(R.id.adults_text)
        val kidsText = popupView.findViewById<TextView>(R.id.kids_text)
        val agencyText = popupView.findViewById<TextView>(R.id.agency_text)
        val hotelText = popupView.findViewById<TextView>(R.id.hotel_text)
        val roomText = popupView.findViewById<TextView>(R.id.room_text)
        val languageText = popupView.findViewById<TextView>(R.id.language_text)
        val dateText = popupView.findViewById<TextView>(R.id.date_text)
        val pickupText = popupView.findViewById<TextView>(R.id.pickup_text)
        val amountText = popupView.findViewById<TextView>(R.id.amount_text)

        val doCheckIn = popupView.findViewById<Button>(R.id.do_check_in_button)
        val undoCheckIn = popupView.findViewById<Button>(R.id.undo_check_in_button)


        tourText.text = reservation.tourName
        confNumText.text = reservation.confNum
        couponText.text = reservation.coupon
        guestText.text = reservation.guestName
        emailText.text = reservation.email
        phoneText.text = reservation.phone
        adultsText.text = reservation.adultNum.toString()
        kidsText.text = reservation.childNum.toString()
        agencyText.text = reservation.agencyName
        hotelText.text = reservation.hotelName
        roomText.text = reservation.room
        languageText.text = reservation.language
        dateText.text = reservation.registrationDate.replace("T"," ")
        pickupText.text = reservation.pickup
        amountText.text = reservation.amount.toString()

        popupWindow.showAtLocation(location, Gravity.CENTER, 0, 0)

        doCheckIn.setOnClickListener {
            val contents = JSONObject()
            contents.put("idReserva", reservation.reservDetailId)
            contents.put("observaciones", "Abordado")
            val changeStatusURL = "http://exinnot.ddns.net:10900/Reservations/ChangeStatusCheckIn?idReserva=${reservation.reservDetailId}&observaciones=Abordado"
            val changeStatusRequest = JsonObjectRequest(
                Request.Method.PUT, changeStatusURL, null,
                { response ->
                    Log.d("Response", response.toString())
                    Toast.makeText(activity.applicationContext, "Pax abordado", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                    printPasses(reservation)
                },
                { error ->
                    Log.d("Error", error.toString())
                    Toast.makeText(activity.applicationContext, "Error al realizar abordado, intente de nuevo", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                })
            queue.add(changeStatusRequest)
        }

        undoCheckIn.setOnClickListener {
            val contents = JSONObject()
            contents.put("idReserva", reservation.reservDetailId)
            val changeStatusURL = "http://exinnot.ddns.net:10900/Reservations/CancelStatusCheckIn?idReserva=${reservation.reservDetailId}"
            val changeStatusRequest = JsonObjectRequest(
                Request.Method.PUT, changeStatusURL, null,
                { response ->
                    Log.d("Response", response.toString())
                    Toast.makeText(activity.applicationContext, "Pax cancelado", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                    doPrinting(reservation)
                },
                { error ->
                    Log.d("Error", error.toString())
                    Toast.makeText(activity.applicationContext, "Error al cancelar abordado, intente de nuevo", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                })
            queue.add(changeStatusRequest)
        }
    }

    fun doPrinting(reservation: Reservation) {

        try {
            findPrinter()
            openPrinter()
            printPasses(reservation)
            disconnectBT()
        }catch (e: Exception){
            Log.d("Error at print", e.toString())
        }


    }

    fun findPrinter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter!!.isEnabled) {

            val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(activity,enableBT,0,null)
        }

        val pairedDevice = bluetoothAdapter!!.bondedDevices

        if (pairedDevice.size > 0) {
            for (pairedDev in pairedDevice) {

                // My Bluetoth printer name is BTP_F09F1A
                if (pairedDev.name == "MTP-2") {
                    bluetoothDevice = pairedDev
                    break
                }
            }
        }
    }

    fun openPrinter() {
        //Standard uuid from string //
        val uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        bluetoothSocket = bluetoothDevice!!.createRfcommSocketToServiceRecord(uuidSting)
        bluetoothSocket!!.connect()
        outputStream = bluetoothSocket!!.outputStream
        inputStream = bluetoothSocket!!.inputStream

        beginListenData()
    }

    fun beginListenData() {
            val handler = Handler()
            val delimiter: Byte = 10
            stopWorker = false
            readBufferPosition = 0
            readBuffer = ByteArray(1024)
            thread = Thread {
                while (!Thread.currentThread().isInterrupted && !stopWorker) {
                    try {
                        val byteAvailable = inputStream!!.available()
                        if (byteAvailable > 0) {
                            val packetByte = ByteArray(byteAvailable)
                            inputStream!!.read(packetByte)
                            for (i in 0 until byteAvailable) {
                                val b = packetByte[i]
                                if (b == delimiter) {
                                    val encodedByte =
                                        ByteArray(readBufferPosition)
                                    System.arraycopy(
                                        readBuffer, 0,
                                        encodedByte, 0,
                                        encodedByte.size
                                    )
                                    val data =
                                        String(encodedByte, StandardCharsets.US_ASCII)
                                    readBufferPosition = 0
                                    handler.post {
                                        Log.d("Connected to", data)
                                    }
                                } else {
                                    readBuffer[readBufferPosition++] = b
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        stopWorker = true
                    }
                }
            }
            thread!!.start()
    }

    fun printPasses(reservation: Reservation) {
        val escPos = EscPos(outputStream)
        val paxNum = reservation.adultNum+reservation.childNum
        for (i in 1 until paxNum){
            escPos.style = Style().setColorMode(Style.ColorMode.WhiteOnBlack).setFontSize(Style.FontSize._3, Style.FontSize._3)
            escPos.write("Y11")
            escPos.feed(1)
            escPos.style = Style().setBold(true).setFontSize(Style.FontSize._1, Style.FontSize._1)
            escPos.write("Vallarta Adventures Boarding Pass")
            escPos.style = Style().setBold(false).setFontSize(Style.FontSize._1, Style.FontSize._1)
            escPos.feed(1)
            escPos.write("Name: ${reservation.guestName}")
            escPos.feed(1)
            escPos.write("Hotel: ${reservation.hotelName}")
            escPos.feed(1)
            escPos.write("Tour: ${reservation.tourName}")
            escPos.feed(1)
            escPos.write("Extra Activity:")
            escPos.feed(1)
            escPos.write("Date(d/m/y): ${reservation.registrationDate.split("T")[0]}")
            escPos.feed(1)
            escPos.write("Time of Tour: ${reservation.registrationDate.split("T")[1]}")
            escPos.feed(1)
            escPos.write("Confirmation No.: ${reservation.confNum}")
            escPos.feed(1)
            escPos.write("Passenger No. $i/$paxNum")
            escPos.feed(2)
            escPos.write("Please be ready 10 minutes before departure in:")
            escPos.feed(2)
            escPos.write("Terminal Maritima")
            escPos.feed(1)
            val barCode = BarCode()
            escPos.write(barCode, reservation.confNum)
            escPos.feed(2)
            escPos.cut(EscPos.CutMode.FULL)
            escPos.feed(2)
        }
    }

    fun disconnectBT() {
        stopWorker = true
        outputStream!!.close()
        inputStream!!.close()
        bluetoothSocket!!.close()
    }

}