package com.rvtechnologies.grigora.view.ui.notifications

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rvtechnologies.grigora.NotificationsModel
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.NotificationTitleModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.TimeAgo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.order_details_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(val list: ArrayList<Notification>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TITLE = 1
    val BODY = 2

    private var yesterday = Calendar.getInstance()

    init {
        yesterday.add(Calendar.DAY_OF_MONTH, -1)
    }


    override fun getItemViewType(position: Int): Int {
        return if (list[position] is NotificationsModel)
            BODY
        else
            TITLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TITLE) {
            return TitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_title_notiication,
                    parent,
                    false
                )
            )
        } else {
            return BodyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_body_notification,
                    parent,
                    false
                )
            )

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is TitleViewHolder) {
            holder.tv_title.text = (list[position] as NotificationTitleModel).name
        } else {
            holder as BodyViewHolder

            val circularProgressDrawable = CircularProgressDrawable(holder.tv_name.context!!)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            if (!(list[position] as NotificationsModel).image.isNullOrBlank())
                Picasso.get()
                    .load((list[position] as NotificationsModel).image).placeholder(
                        circularProgressDrawable
                    )
                    .error(
                        circularProgressDrawable
                    )
                    .into(holder.img_data)

            val utcFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    holder.img_data.context!!.resources.configuration.locales[0]
                )
            } else
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    holder.img_data.context!!.resources.configuration.locale
                )

            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")

            var utcDate = utcFormatter.parse((list[position] as NotificationsModel).createdAt)


            var format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    holder.img_data.context!!.resources.configuration.locales[0]
                )
            } else
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    holder.img_data.context!!.resources.configuration.locale
                )

            format.timeZone = TimeZone.getDefault()


            if (!(list[position] as NotificationsModel).timeToShow.isNullOrBlank()) {
                holder.tv_time.text = holder.tv_time.context.getString(R.string.yesterday)
            } else {

                holder.tv_time.text =
                    TimeAgo.getTimeAgo(format.parse(format.format(utcDate)).time)
            }



            holder.tv_name.text = (list[position] as NotificationsModel).notification
        }
    }


    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title = view.findViewById<TextView>(R.id.tv_title)

    }

    inner class BodyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_data = view.findViewById<ImageView>(R.id.img_data)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var tv_time = view.findViewById<TextView>(R.id.tv_time)

    }


}