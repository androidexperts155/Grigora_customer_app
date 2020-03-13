package com.rvtechnologies.grigora.view.ui.notifications

import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.daimajia.swipe.SwipeLayout
import com.rvtechnologies.grigora.NotificationsModel
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.NotificationTitleModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.TimeAgo
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(
    val list: ArrayList<Notification>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
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

            holder.swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
                override fun onOpen(layout: SwipeLayout?) {

                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                }

                override fun onStartOpen(layout: SwipeLayout?) {
                }

                override fun onStartClose(layout: SwipeLayout?) {
                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                }

                override fun onClose(layout: SwipeLayout?) {
                }
            })



            holder.img_delete.setOnClickListener {
                iRecyclerItemClick.onItemClick(position)
            }


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

            var utcDate = CommonUtils.getUtcDate(
                holder.img_data.context!!,
                (list[position] as NotificationsModel).createdAt,
                "yyyy-MM-dd HH:mm:ss"
            )


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


            if (!(list[position] as NotificationsModel).timeToShow.isBlank()) {
                holder.tv_time.text = holder.tv_time.context.getString(R.string.yesterday)
            } else {

                holder.tv_time.text =
                    TimeAgo.getTimeAgo(format.parse(format.format(utcDate)).time)
            }

            holder.tv_timer.visibility = View.GONE
            if ((list[position] as NotificationsModel).timer == 1) {

                setTimer(list[position] as NotificationsModel, holder.tv_timer)

            }

            holder.tv_name.text = (list[position] as NotificationsModel).notification
        }
    }

    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title = view.findViewById<TextView>(R.id.tv_title)

    }

    inner class BodyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_delete = view.findViewById<ImageView>(R.id.img_delete)
        var img_data = view.findViewById<ImageView>(R.id.img_data)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var tv_time = view.findViewById<TextView>(R.id.tv_time)
        var tv_timer = view.findViewById<TextView>(R.id.tv_timer)
        var swipeLayout = view.findViewById<SwipeLayout>(R.id.swipeLayout)
    }

    private fun setTimer(notificationsModel: NotificationsModel, tv_timer: TextView) {
        var format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")


        var currentTime = Calendar.getInstance()

        var currentTimeUtc = Calendar.getInstance()
        currentTimeUtc.time = CommonUtils.getUtcDate(
            tv_timer.context!!,
            format.format(currentTime.time),
            "dd/MM/yyyy HH:mm:ss"
        )

        currentTimeUtc.add(Calendar.MINUTE, 10)


        var notificationTimeUtc = CommonUtils.getUtcDate(
            tv_timer.context!!,
            notificationsModel.createdAt,
            "dd/MM/yyyy HH:mm:ss"
        )


        var seconds = currentTimeUtc.time.time - notificationTimeUtc.time

        if (seconds > 0) {
            tv_timer.visibility = View.VISIBLE

            seconds /= 1000

            object : CountDownTimer((seconds * 1000).toLong(), 1000) {
                override fun onFinish() {
                }

                override fun onTick(millisUntilFinished: Long) {

                    val p1 = (millisUntilFinished / 1000) % 60
                    var p2 = (millisUntilFinished / 1000) / 60
                    val p3 = p2 % 60
                    p2 = p2 / 60
                    tv_timer.text = "$p2:$p3:$p1"
                }
            }.start()
        }

    }


}