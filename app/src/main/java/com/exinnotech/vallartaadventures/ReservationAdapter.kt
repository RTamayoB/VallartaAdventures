package com.exinnotech.vallartaadventures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.exinnotech.vallartaadventures.room.entity.Reservation

class ReservationAdapter(var data: List<Reservation>): RecyclerView.Adapter<MyViewHolder>(), Filterable {

    var dataFiltered = emptyList<Reservation>()

    init {
        dataFiltered = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataFiltered[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val searchItem = p0.toString()
                if(searchItem.isEmpty()){
                    dataFiltered = data
                }else{
                    val result = ArrayList<Reservation>()
                    for(item in data){
                        if(item.confNum.lowercase().contains(searchItem.lowercase())){
                            result.add(item)
                        }
                    }
                    dataFiltered = result.toList()
                }
                val filterResults = FilterResults()
                filterResults.values = dataFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                dataFiltered = p1?.values as List<Reservation>
                notifyDataSetChanged()
            }

        }
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