package com.rvtechnologies.grigora.view.ui.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_rate_driver_dialog.*

class RateDriverDialogFragment(val orderItemModel: OrderItemModel, val driverRate: DriverRate) :
    DialogFragment() {
    var cList = ArrayList<TextView>()
    var chList = ArrayList<TextView>()
    var tList = ArrayList<TextView>()

    var tip = ""
    var goodReview = ""
    var badReview = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rate_driver_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)


        initList()
        initClicks()

        tv_name.text = getString(R.string.rate_driver, orderItemModel.driverName)
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Picasso.get()
            .load(orderItemModel.driverImage).placeholder(
                circularProgressDrawable
            )
            .error(
                circularProgressDrawable
            )
            .into(img_driver)

        bt_no_thanks.setOnClickListener {
            driverRate.onDriverRateCancel(orderItemModel)
            this.dismiss()
        }

        bt_rate_now.setOnClickListener {
            if (!ed_tip.text.toString().isNullOrEmpty())
                tip = ed_tip.text.toString()

            driverRate.onDriverRateSubmit(
                rt_rating.rating,
                goodReview,
                badReview,
                orderItemModel,
                tip
            )
            this.dismiss()
        }
    }

    private fun initList() {
        cList.add(c1)
        cList.add(c2)
        cList.add(c3)
        cList.add(c4)

        chList.add(ch1)
        chList.add(ch2)
        chList.add(ch3)

        tList.add(t1)
        tList.add(t2)
        tList.add(t3)
        tList.add(t4)
    }

    private fun initClicks() {

        c1.setOnClickListener { selectC(c1) }
        c2.setOnClickListener { selectC(c2) }
        c3.setOnClickListener { selectC(c3) }
        c4.setOnClickListener { selectC(c4) }

        ch1.setOnClickListener { selectCh(ch1) }
        ch2.setOnClickListener { selectCh(ch2) }
        ch3.setOnClickListener { selectCh(ch3) }

        t1.setOnClickListener { selectT(t1) }
        t2.setOnClickListener { selectT(t2) }
        t3.setOnClickListener { selectT(t3) }
        t4.setOnClickListener { selectT(t4) }
    }

    private fun selectC(view: TextView) {
        badReview = view.text.toString()

        for (item in cList) {
            if (item.id == view.id) {
                item.setBackgroundResource(R.drawable.chip_selected_bg)
                item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                item.setBackgroundResource(R.drawable.chip_deselected_bg)
                if (CommonUtils.isDarkMode())
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            }
        }
    }

    private fun selectCh(view: TextView) {
        goodReview = view.text.toString()
        for (item in chList) {
            if (item.id == view.id) {
                item.setBackgroundResource(R.drawable.chip_selected_bg)
                item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                item.setBackgroundResource(R.drawable.chip_deselected_bg)
                if (CommonUtils.isDarkMode())
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            }
        }
    }

    private fun selectT(view: TextView) {
        tip = view.tag.toString()
        for (item in tList) {
            if (item.id == view.id) {
                item.setBackgroundResource(R.drawable.chip_selected_bg)
                item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                item.setBackgroundResource(R.drawable.chip_deselected_bg)
                if (CommonUtils.isDarkMode())
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            }
        }
    }

    interface DriverRate {
        fun onDriverRateSubmit(
            rating: Float,
            goodReview: String,
            badReview: String,
            orderItemModel: OrderItemModel, tip: String
        )

        fun onDriverRateCancel(orderItemModel: OrderItemModel)
    }


}