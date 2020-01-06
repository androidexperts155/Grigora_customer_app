package com.rvtechnologies.grigora.view.ui.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel
import kotlinx.android.synthetic.main.fragment_rate_driver_dialog.*

class RateDriverDialogFragment(val orderItemModel: OrderItemModel, val driverRate: DriverRate) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rate_driver_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_no_thanks.setOnClickListener {
            driverRate.onDriverRateCancel(orderItemModel)
            this.dismiss()
        }

        bt_rate_now.setOnClickListener {
            driverRate.onDriverRateSubmit(rt_rating.rating,orderItemModel)
            this.dismiss()
        }

    }


    interface DriverRate {
        fun onDriverRateSubmit(rating: Float,  orderItemModel: OrderItemModel)
        fun onDriverRateCancel(orderItemModel: OrderItemModel)
    }
}