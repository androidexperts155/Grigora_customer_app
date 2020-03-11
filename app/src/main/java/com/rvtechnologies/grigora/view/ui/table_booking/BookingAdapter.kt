package com.rvtechnologies.grigora.view.ui.table_booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.TableBookingHistoryModel
import com.rvtechnologies.grigora.utils.CommonUtils

class BookingAdapter(var list: ArrayList<TableBookingHistoryModel>) :
    RecyclerView.Adapter<BookingAdapter.BookingView>() {

    inner class BookingView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_restaurant_name = view.findViewById<TextView>(R.id.tv_restaurant_name)
        var tv_status = view.findViewById<TextView>(R.id.tv_status)
        var tv_restaurant_address = view.findViewById<TextView>(R.id.tv_restaurant_address)
        var tv_people = view.findViewById<TextView>(R.id.tv_people)
        var tv_date = view.findViewById<TextView>(R.id.tv_date)
        var tv_time = view.findViewById<TextView>(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingView {
        return BookingView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_table_booking_history,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BookingView, position: Int) {
//        1: placed, 2:accepted, 3:rejected by restaurant, 4:time over and custmer didn't come

        holder.tv_restaurant_name.text = list[position].restaurant_name
        holder.tv_people.text = "Booked for " + list[position].no_of_seats.toString()
        holder.tv_restaurant_address.text = list[position].restaurant_address
        holder.tv_restaurant_name.text = list[position].restaurant_name
        holder.tv_date.text =
            CommonUtils.getFormattedUtc(list[position].date, "yyyy-MM-dd", "yyyy-MM-dd")

//        holder.tv_time.text =
//            CommonUtils.getFormattedUtc(list[position].start_time_from, "HH:mm-ss", "HH:mm:ss")+" - "+CommonUtils.getFormattedUtc(list[position].start_time_to, "HH:mm-ss", "HH:mm:ss")

                holder.tv_time.text =
            list[position].start_time_from+" - "+list[position].start_time_to



    }

}