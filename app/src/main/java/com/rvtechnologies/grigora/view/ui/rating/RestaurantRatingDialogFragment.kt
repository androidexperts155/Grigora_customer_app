package com.rvtechnologies.grigora.view.ui.rating


import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
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
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_reatsurant_rating_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class RestaurantRatingDialogFragment(
    val orderItemModel: OrderItemModel,
    val restaurantRate: RestaurantRate
) : DialogFragment() {
    var review = ""
    var goodReview = ""
    var cList = ArrayList<TextView>()
    var selectedView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reatsurant_rating_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        tv_name.text = getString(R.string.rate_driver, orderItemModel.restaurantName)
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Picasso.get()
            .load(orderItemModel.restaurantImage).placeholder(
                circularProgressDrawable
            )
            .error(
                circularProgressDrawable
            )
            .into(img_rest)

        initList()
        initClicks()

        bt_no_thanks.setOnClickListener {
            restaurantRate.onRateRestaurantCancel(orderItemModel)
            this.dismiss()
        }

        bt_rate_now.setOnClickListener {
            restaurantRate.onrestaurantRateSubmit(
                rt_rating.rating,
                goodReview,
                ed_review.text.toString(),
                orderItemModel
            )
            this.dismiss()
        }
    }

    private fun initList() {
        cList.add(c1)
        cList.add(c2)
        cList.add(c3)
        cList.add(c4)
    }

    private fun initClicks() {

        c1.setOnClickListener { selectC(c1) }
        c2.setOnClickListener { selectC(c2) }
        c3.setOnClickListener { selectC(c3) }
        c4.setOnClickListener { selectC(c4) }
    }

    fun selectC(view: TextView) {

        for (item in cList) {
            if (item.id == view.id) {
//                selected again
                if (selectedView != null && selectedView == view) {
                    selectedView = null
                    goodReview = ""
                    item.setBackgroundResource(R.drawable.chip_deselected_bg)
                    if (CommonUtils.isDarkMode())
                        item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    else
                        item.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
                } else {
                    goodReview = view.text.toString()
                    item.setBackgroundResource(R.drawable.chip_selected_bg)
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    selectedView = view
                }

            } else {
                item.setBackgroundResource(R.drawable.chip_deselected_bg)
                if (CommonUtils.isDarkMode())
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    item.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }
        }
    }


    interface RestaurantRate {
        fun onrestaurantRateSubmit(
            rating: Float, goodReview: String,
            badReview: String, orderItemModel: OrderItemModel
        )

        fun onRateRestaurantCancel(orderItemModel: OrderItemModel)
    }

    override fun onResume() {
        super.onResume()

        var displayMetrics = DisplayMetrics()
        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = (displayMetrics.widthPixels - (displayMetrics.widthPixels / 9))


        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = width
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

}
