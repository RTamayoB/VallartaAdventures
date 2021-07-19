package com.exinnotech.vallartaadventures.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.R
import com.exinnotech.vallartaadventures.room.entity.Reservation

class ReservationAdapter(data: List<Reservation>, onItemListener: OnItemListener): RecyclerView.Adapter<ReservationAdapter.MyViewHolder>(), Filterable {

    var data = emptyList<Reservation>()
    var dataFiltered = emptyList<Reservation>()
    private val mOnItemListener: OnItemListener

    init {
        this.data = data
        dataFiltered = this.data
        mOnItemListener = onItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item, parent, false)

        return MyViewHolder(view, mOnItemListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataFiltered[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataFiltered.size
    }

    //Filtering works as follows name/confNum,zone,hotel,tour
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults? {
                val searchItem = p0.toString()
                if(searchItem.isEmpty()){
                    dataFiltered = data
                }else{
                    val result = ArrayList<Reservation>()
                    for(item in data){
                        //TODO(Add filtering for other filters)
                        if(item.confNum.lowercase().contains(searchItem.lowercase())
                            || item.guestName.lowercase().contains(searchItem.lowercase())){
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

    inner class MyViewHolder(itemView: View, onItemListener: OnItemListener):
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameText: TextView = itemView.findViewById(R.id.name_text)
        val confNumText: TextView = itemView.findViewById(R.id.conf_num_text)
        val tourText: TextView = itemView.findViewById(R.id.tour_txt)
        private var mOnItemListener: OnItemListener = onItemListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mOnItemListener.onItemClick(dataFiltered[bindingAdapterPosition])
        }

        fun bind(item: Reservation){
            confNumText.text = item.confNum
            nameText.text = item.guestName
            tourText.text = item.tourName
        }
    }

    interface OnItemListener {
        fun onItemClick(reservation: Reservation)
    }

}
