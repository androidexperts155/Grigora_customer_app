package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

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

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.MyPurchasedCardModel
import com.rvtechnologies.grigora.model.NotificationTitleModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.TimeAgo
import com.rvtechnologies.grigora.view.ui.notifications.Notification
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyCardsAdapter(
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
        return if (list[position] is MyPurchasedCardModel)
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
                    R.layout.item_my_card,
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


            val circularProgressDrawable = CircularProgressDrawable(holder.tv_amount.context!!)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            if (!(list[position] as MyPurchasedCardModel).voucher_image.isNullOrBlank())
                Picasso.get()
                    .load((list[position] as MyPurchasedCardModel).voucher_image).placeholder(
                        circularProgressDrawable
                    )
                    .error(
                        circularProgressDrawable
                    )
                    .into(holder.img_card)

            var utcDate = CommonUtils.utcToLocal(
                holder.img_card.context!!,
                (list[position] as MyPurchasedCardModel).created_at,
                "yyyy-MM-dd HH:mm:ss"
            )


            var format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    holder.img_card.context!!.resources.configuration.locales[0]
                )
            } else
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    holder.img_card.context!!.resources.configuration.locale
                )

            format.timeZone = TimeZone.getDefault()
            holder.tv_amount.text="₦ "+ (list[position] as MyPurchasedCardModel).amount


            holder.img_redeem.setOnClickListener {
                var data=(list[position] as MyPurchasedCardModel)
                data.isShare=false
                iRecyclerItemClick.onItemClick(data)
            }

            holder.img_share.setOnClickListener {
                var data=(list[position] as MyPurchasedCardModel)
                data.isShare=true
                iRecyclerItemClick.onItemClick(data)
            }

         }
    }

    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title = view.findViewById<TextView>(R.id.tv_title)

    }

    inner class BodyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_card = view.findViewById<ImageView>(R.id.img_card)
        var img_redeem = view.findViewById<ImageView>(R.id.img_redeem)
        var img_share = view.findViewById<ImageView>(R.id.img_share)
        var tv_amount = view.findViewById<TextView>(R.id.tv_amount)


    }



}