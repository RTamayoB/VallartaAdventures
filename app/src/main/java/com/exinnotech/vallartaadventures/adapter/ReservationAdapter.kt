package com.exinnotech.vallartaadventures.adapter

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.exinnotech.vallartaadventures.R
import com.exinnotech.vallartaadventures.room.entity.Reservation

/**
 * Adapter to use in the Recycler view for the reservation list
 *
 * @constructor
 * Initializes the data
 *
 * @param data List of reservations
 * @param onItemListener ItemListener to add onClick functionality
 */
class ReservationAdapter(data: List<Reservation>, onItemListener: OnItemListener): RecyclerView.Adapter<ReservationAdapter.MyViewHolder>()/*, Filterable*/ {

    var data = emptyList<Reservation>()
    var dataFiltered = emptyList<Reservation>()
    private val mOnItemListener: OnItemListener

    init {
        this.data = data
        val result = ArrayList<Reservation>()
        for(item in data){
            if(item.status != 14){
                result.add(item)
            }
        }
        dataFiltered = result.toList()
        mOnItemListener = onItemListener
    }

    /**
     * Inflates the layout of the item
     *
     * @param parent Parent of the layout
     * @param viewType
     * @return¨The ViewHolder (MyViewHolder class)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item, parent, false)

        return MyViewHolder(view, mOnItemListener)
    }

    /**
     * Binds the data to the item
     *
     * @param holder The ViewHolder
     * @param position Position of the item
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataFiltered[position]
        holder.bind(item)
    }

    /**
     * @return The number of items on the list
     */
    override fun getItemCount(): Int {
        return dataFiltered.size
    }

    /*
    /**
     * Filters the list of reservations by the specified parameters
     *
     * @return Filtered list
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults? {
                val searchItem = p0.toString()
                if(searchItem.isEmpty()){
                    dataFiltered = data
                }else{
                    val result = ArrayList<Reservation>()
                    for(item in data){
                        //TODO Add filtering for other filters
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
    */

    /**
     * My filter for all the diferent type of filters
     *
     * @param queryText Text of the Searchview, can be the guest_name or confirmation_code
     * @param hotelZone Hotel zone
     * @param hotel Name of the hotel
     * @param tour Name of the tour
     * @param board Look for boarded
     */
    fun filter(queryText: String, hotelZone: String, hotel: String, tour: String, board: Boolean) {
        val result = ArrayList<Reservation>()
        for(item in data){
            //Query Name or Code
            if(item.confNum.lowercase().contains(queryText.lowercase()) || item.guestName.lowercase().contains(queryText.lowercase())){
                result.add(item)
            }
            //Query hotel zone
            if(hotelZone != "TODOS" || hotelZone.isNotEmpty()){

            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query hotels
            if(hotel != "TODOS" || hotel.isNotEmpty()){
                if(item.hotelName == hotel) {
                    if (!(result.contains(item))) {
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query tours
            if(tour != "TODOS" || tour.isNotEmpty()){
                if(item.tourName == tour){
                    if(!(result.contains(item))){
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query board
            if(board){
                if(item.status == 14){
                    if(!(result.contains(item))){
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
                if(item.status == 14){
                    result.remove(item)
                }
            }
        }
        dataFiltered = result.toList()
        notifyDataSetChanged()
    }

    fun filterWithoutQuery(hotelZone: String, hotel: String, tour: String, board: Boolean) {
        val result = ArrayList<Reservation>()
        for(item in data){
            //Query hotel zone
            if(hotelZone != "TODOS" || hotelZone.isNotEmpty()){

            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query hotels
            if(hotel != "TODOS" || hotel.isNotEmpty()){
                if(item.hotelName == hotel) {
                    if (!(result.contains(item))) {
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query tours
            if(tour != "TODOS" || tour.isNotEmpty()){
                if(item.tourName == tour){
                    if(!(result.contains(item))){
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
            }
            //Query board
            if(board){
                if(item.status == 14){
                    if(!(result.contains(item))){
                        result.add(item)
                    }
                }
            }
            else{
                if(!(result.contains(item))){
                    result.add(item)
                }
                if(item.status == 14){
                    result.remove(item)
                }
            }
        }
        dataFiltered = result.toList()
        notifyDataSetChanged()
    }

    fun myFilter(searchItem: String, nameChecked: Boolean, confNumChecked: Boolean, hotelZoneChecked: Boolean, hotelChecked: Boolean, tourChecked: Boolean){
        val result = ArrayList<Reservation>()
        if(!nameChecked && !confNumChecked && !hotelZoneChecked && !hotelChecked && !tourChecked){
            for(item in data){
                //TODO Add filtering for other filters
                if(item.confNum.lowercase().contains(searchItem.lowercase()) || item.guestName.lowercase().contains(searchItem.lowercase())){
                    result.add(item)
                }
            }
        }
        else{
            for(item in data){
                if(nameChecked){
                    if(item.guestName.lowercase().contains(searchItem.lowercase())){
                        result.add(item)
                    }
                }
                if(confNumChecked){
                    if(item.confNum.lowercase().contains(searchItem.lowercase())){
                        result.add(item)
                    }
                }
                if(hotelZoneChecked){
                    /*
                    if(item.guestName.lowercase().contains(searchItem.lowercase())){
                        result.add(item)
                    }
                     */
                }
                if(hotelChecked){
                    if(item.hotelName.lowercase().contains(searchItem.lowercase())){
                        result.add(item)
                    }
                }
                if(tourChecked){
                    if(item.tourName.lowercase().contains(searchItem.lowercase())){
                        result.add(item)
                    }
                }
            }
        }
        dataFiltered = result.toList()
        notifyDataSetChanged()
    }

    /**
     * View Holder manages the instantiation of the layout views and binds the data
     *
     * @param itemView View of the item
     * @param onItemListener Listener for the onClick method
     */
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

            if(item.status == 14){
                itemView.setBackgroundResource(R.drawable.boarded_reservation_border)
            }else{
                itemView.setBackgroundResource(R.drawable.reservation_border)
            }
        }
    }

    interface OnItemListener {
        fun onItemClick(reservation: Reservation)
    }

}
