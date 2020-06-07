package com.rvtechnologies.grigora.view.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_schedule_success.*
import java.text.SimpleDateFormat
import java.util.*
/**
 * A simple [Fragment] subclass.
 */
class ScheduleSuccess(
    var iRecyclerItemClick: IRecyclerItemClick,
    var date: String,
    var time: String,
    var isPickup:Boolean
) : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bt_submit.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("1")
        }

        var date = CommonUtils.getFormattedDate(date, "yyyy-MM-dd", "dd MMM")
        if(isPickup)
            tv_rate.text =
                getString(R.string.schedule_request_sent_info) + " " + date + " " + getString(R.string.can_be_picked_in)
            else
        tv_rate.text =
            getString(R.string.schedule_request_sent_info) + " " + date + " " + getString(R.string.schedule_request_sent_info_more)
//        HH:mm:ss
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var scheduleDate = sdf.parse("${this.date} $time")
        var currentTime = Date()
        var d = (scheduleDate.time - currentTime.time)

        val p1 = (d / 1000) % 60
        var p2 = (d / 1000) / 60
        val p3 = p2 % 60
        p2 /= 60

        var pp2 = if (p2 > 9) p2.toString() else "0$p2"
        var pp3 = if (p3 > 9) p3.toString() else "0$p3"

        tv_time.text =
            "${pp2}hr:${pp3}min"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_success, container, false)
    }


}
