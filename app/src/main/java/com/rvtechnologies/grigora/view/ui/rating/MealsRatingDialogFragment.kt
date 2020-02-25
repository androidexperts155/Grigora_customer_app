package com.rvtechnologies.grigora.view.ui.rating


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.view.ui.rating.adapter.MealsAdapter
import kotlinx.android.synthetic.main.fragment_meals_rating_dialog.*

class MealsRatingDialogFragment(
    val mealsToRate: ArrayList<OrderItemModel.OrderDetail>,
    val mealsRate: MealsRate
) : DialogFragment(), MealsAdapter.RatingChanged {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meals_rating_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        bt_rate_now.setOnClickListener {
            mealsRate.onMealRateSubmit(mealsToRate)
            this.dismiss()
        }
        bt_no_thanks.setOnClickListener {
            mealsRate.onMealRateCancel()
            this.dismiss()
        }

        rc_meals.adapter = MealsAdapter(mealsToRate, this)
    }

    interface MealsRate {
        fun onMealRateSubmit(mealsToRate: ArrayList<OrderItemModel.OrderDetail>)
        fun onMealRateCancel()
    }

    override fun ratingChanged(position: Int, rating: Float) {
        mealsToRate[position].rating = rating
    }
}

