package com.exinnotech.vallartaadventures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.room.entity.Reservation

class ReservationAdapter(var data: List<Reservation>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val nameText: TextView = itemView.findViewById(R.id.name_text)
    val confNumText: TextView = itemView.findViewById(R.id.conf_num_text)
    val tourText: TextView = itemView.findViewById(R.id.tour_text)

    fun bind(item: Reservation){
        confNumText.text = item.confNum
        nameText.text = item.clientName
    }

    companion object {
        fun create(parent: ViewGroup): MyViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.reservation_item, parent, false)
            return MyViewHolder(view)
        }
    }
}