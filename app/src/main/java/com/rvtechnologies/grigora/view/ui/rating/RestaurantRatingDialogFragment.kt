package com.rvtechnologies.grigora.view.ui.rating


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel
import kotlinx.android.synthetic.main.fragment_reatsurant_rating_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class RestaurantRatingDialogFragment(
    val orderItemModel: OrderItemModel,
    val restaurantRate: RestaurantRate
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reatsurant_rating_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        bt_no_thanks.setOnClickListener {
            restaurantRate.onRateRestaurantCancel(orderItemModel)
            this.dismiss()
        }

        bt_rate_now.setOnClickListener {
            restaurantRate.onrestaurantRateSubmit(rt_rating.rating, orderItemModel)
            this.dismiss()
        }
    }

    interface RestaurantRate {
        fun onrestaurantRateSubmit(rating: Float, orderItemModel: OrderItemModel)
        fun onRateRestaurantCancel(orderItemModel: OrderItemModel)
    }

}
