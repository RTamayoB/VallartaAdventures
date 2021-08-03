package com.exinnotech.vallartaadventures

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.viewmodel.ReservationViewModel
import com.exinnotech.vallartaadventures.scanning.ScanActivity
import org.json.JSONObject
import java.io.IOException

/**
 * This class handles the popup to show the details of the reservation, as well to handle the check in methods
 *
 * @property activity Activity from where the action is being performed
 * @property reservation Reservation picked to draw the data from
 */
class CheckInActivity(val activity: Activity, val reservation: Reservation, viewModel: ReservationViewModel) {

    private val queue = Volley.newRequestQueue(activity.applicationContext)
    val reservationViewModel = viewModel

    /**
     * Inflates the view and shows the popup, and handles the check in methods
     */
    fun showCheckInPopup() {
        val inflater = activity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.client_check_in, null)

        val widht : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height : Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, widht,height,focusable)

        val tourText = popupView.findViewById<TextView>(R.id.tour_text)
        val guestText = popupView.findViewById<TextView>(R.id.guest_text)
        val emailText = popupView.findViewById<TextView>(R.id.email_text)
        val phoneText = popupView.findViewById<TextView>(R.id.phone_text)
        val adultsText = popupView.findViewById<TextView>(R.id.adults_text)
        val kidsText = popupView.findViewById<TextView>(R.id.kids_text)
        val hotelText = popupView.findViewById<TextView>(R.id.hotel_text)
        val roomText = popupView.findViewById<TextView>(R.id.room_text)
        val languageText = popupView.findViewById<TextView>(R.id.language_text)
        val dateText = popupView.findViewById<TextView>(R.id.date_text)
        val pickupText = popupView.findViewById<TextView>(R.id.pickup_text)
        val amountText = popupView.findViewById<TextView>(R.id.amount_text)

        val doCheckIn = popupView.findViewById<Button>(R.id.do_check_in_button)
        val undoCheckIn = popupView.findViewById<Button>(R.id.undo_check_in_button)


        tourText.text = reservation.tourName
        guestText.text = reservation.guestName
        emailText.text = reservation.email
        val phone = SpannableString(reservation.phone)
        phone.setSpan(UnderlineSpan(), 0, phone.length, 0)
        phoneText.text = phone
        adultsText.text = reservation.adultNum.toString()
        kidsText.text = reservation.childNum.toString()
        hotelText.text = reservation.hotelName
        roomText.text = reservation.room
        languageText.text = reservation.language
        dateText.text = reservation.reservationDate.split("T")[0]+" "+reservation.reservationTime
        pickupText.text = reservation.pickup
        amountText.text = reservation.amount.toString()

        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

        phoneText.setOnClickListener {
            if(reservation.phone.isNotEmpty()){
                val uri = "tel: ${reservation.phone}"
                val call = Intent(Intent.ACTION_DIAL)
                call.data = Uri.parse(uri)
                activity.startActivity(call)
            }
        }

        /**
         * Does the check in
         */
        doCheckIn.setOnClickListener {
            try{
                val printer = ScanActivity(activity, reservation)
                printer.connectPrinter()
                val contents = JSONObject()
                contents.put("idReserva", reservation.reservDetailId)
                contents.put("observaciones", "Abordado")
                val changeStatusURL =
                    "http://exinnot.ddns.net:10900/Reservations/ChangeStatusCheckIn?idReserva=${reservation.reservDetailId}&observaciones=Abordado"
                val changeStatusRequest = JsonArrayRequest(
                    Request.Method.PUT, changeStatusURL, null,
                    { response ->
                        Log.d("Response", response.toString())
                        reservation.status = 14
                        reservationViewModel.update(reservation)
                        printer.printPasses(reservation)
                        Toast.makeText(
                            activity.applicationContext,
                            "Pax abordado",
                            Toast.LENGTH_LONG
                        ).show()
                        val adapter = activity.findViewById<RecyclerView>(R.id.reservation_list)
                        adapter.adapter?.notifyDataSetChanged()
                        popupWindow.dismiss()

                    },
                    { error ->
                        Log.d("Error", error.toString())
                        Toast.makeText(
                            activity.applicationContext,
                            "Error al realizar abordado, intente de nuevo",
                            Toast.LENGTH_LONG
                        ).show()
                        popupWindow.dismiss()
                    })
                queue.add(changeStatusRequest)
            }catch (e: IOException){
                popupWindow.dismiss()
                Log.d("Error",e.toString())
                Toast.makeText(activity.applicationContext, "Error conectando a la impresora, revise que este encendida y conectada al dispositivo", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                popupWindow.dismiss()
                Toast.makeText(activity.applicationContext, "Error", Toast.LENGTH_LONG).show()
                Log.d("Error",e.toString())
            }
        }

        /**
         * Cancles the check in
         */
        undoCheckIn.setOnClickListener {
            val contents = JSONObject()
            contents.put("idReserva", reservation.reservDetailId)
            val changeStatusURL = "http://exinnot.ddns.net:10900/Reservations/CancelStatusCheckIn?idReserva=${reservation.reservDetailId}"
            val changeStatusRequest = JsonObjectRequest(
                Request.Method.PUT, changeStatusURL, null,
                { response ->
                    Log.d("Response", response.toString())
                    reservation.status = 1
                    reservationViewModel.update(reservation)
                    Toast.makeText(activity.applicationContext, "Pax cancelado", Toast.LENGTH_LONG).show()
                    val adapter = activity.findViewById<RecyclerView>(R.id.reservation_list)
                    adapter.adapter?.notifyDataSetChanged()
                    popupWindow.dismiss()
                },
                { error ->
                    Log.d("Error", error.toString())
                    Toast.makeText(activity.applicationContext, "Error al cancelar abordado, intente de nuevo", Toast.LENGTH_LONG).show()
                    popupWindow.dismiss()
                })
            queue.add(changeStatusRequest)
        }
    }
}