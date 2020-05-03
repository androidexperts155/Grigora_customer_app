package com.rvtechnologies.grigora.view.ui.table_booking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.TableBookingHistoryModel
import com.rvtechnologies.grigora.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_booking_detail_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class BookingDetailDialog(var bookingModel: TableBookingHistoryModel) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_detail_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        CommonUtils.loadImage(img_rest, bookingModel.restaurant_image)
        tv_restname.text = bookingModel.restaurant_name
        tv_address.text = bookingModel.restaurant_address
        tv_number.text = bookingModel.no_of_seats.toString()
        tv_date.text = bookingModel.date
        tv_time.text = CommonUtils.getFormattedUtc(
            bookingModel.start_time_from,
            "HH:mm:ss",
            "hh:mm aa"
        ) + " - " + CommonUtils.getFormattedUtc(bookingModel.start_time_to, "HH:mm:ss", "hh:mm aa")


        when (bookingModel.booking_status) {
            1 -> {
                tv_status.text = getString(R.string.pending)
                tv_status.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimaryDark
                    )
                )
                tv_status.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_hourglass),
                    null, null, null
                )
            }
            2 -> {
                tv_status.text = getString(R.string.approved)
                tv_status.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.green
                    )
                )
                tv_status.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_tick),
                    null, null, null
                )
            }
            3 -> {
                tv_status.text = getString(R.string.disapproved)
                tv_status.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimaryDark
                    )
                )
                tv_status.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_rejected),
                    null, null, null
                )
            }
            4 -> {
                tv_status.text = context!!.getString(R.string.disapproved)
                tv_status.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimaryDark
                    )
                )
                tv_status.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_rejected),
                    null, null, null
                )
            }

        }

        img_close.setOnClickListener { dismiss() }
    }

}
