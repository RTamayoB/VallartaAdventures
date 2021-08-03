package com.exinnotech.vallartaadventures.scanning

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import com.github.anastaciocintra.escpos.barcode.BarCode
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

class ScanActivity(val activity: Activity, val reservation: Reservation) {

    fun connectPrinter() {

        findPrinter()
        openPrinter()
    }

    private fun findPrinter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter!!.isEnabled) {

            val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(activity, enableBT, 0, null)
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

    private fun openPrinter() {
        //Standard uuid from string //
        val uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        bluetoothSocket = bluetoothDevice!!.createRfcommSocketToServiceRecord(uuidSting)
        bluetoothSocket!!.connect()
        outputStream = bluetoothSocket!!.outputStream
        inputStream = bluetoothSocket!!.inputStream

        beginListenData()
    }

    private fun beginListenData() {
        val handler = Handler(Looper.getMainLooper())
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
        val paxNum = reservation.adultNum+reservation.childNum
        for (i in 0 until paxNum){
            val escPos = EscPos(outputStream)
            escPos.style = Style().setColorMode(Style.ColorMode.WhiteOnBlack).setFontSize(Style.FontSize._3, Style.FontSize._3)
            escPos.write(reservation.vehicleCode)
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
            escPos.write("Date(d/m/y): ${reservation.reservationDate.split("T")[0]}")
            escPos.feed(1)
            escPos.write("Time of Tour: ${reservation.reservationTime}")
            escPos.feed(1)
            escPos.write("Confirmation No.: ${reservation.confNum}")
            escPos.feed(1)
            escPos.write("Passenger No. ${i+1}/$paxNum")
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
            Thread.sleep(5000)
        }
        disconnectBT()
    }

    private fun disconnectBT() {
        stopWorker = true
        outputStream!!.close()
        inputStream!!.close()
        bluetoothSocket!!.close()
    }

}