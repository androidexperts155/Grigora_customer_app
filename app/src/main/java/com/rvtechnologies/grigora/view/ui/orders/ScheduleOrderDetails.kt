package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderItemAdapter
import com.rvtechnologies.grigora.view_model.ScheduleOrderDetailsViewModel
import kotlinx.android.synthetic.main.schedule_order_details_fragment.*

class ScheduleOrderDetails : Fragment() {
    companion object {
        fun newInstance() = ScheduleOrderDetails()
    }

    private lateinit var viewModel: ScheduleOrderDetailsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_order_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScheduleOrderDetailsViewModel::class.java)


        viewModel.cancelOrderRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                view?.findNavController()?.popBackStack()
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.scheduled_order))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var orderDetail = arguments?.get(AppConstants.SCHEDULED_ORDER_MODEL) as OrderItemModel
        tv_date.text = CommonUtils.getFormattedUtc(
            orderDetail.scheduleTime,
            "yyyy-MM-dd HH:mm:ss",
            "hh:mm aa, dd MMMM YYYY"
        )


        tv_order_id.text = orderDetail.idToShow
        tv_name.text = orderDetail.restaurantName
        tv_cuisines.text = orderDetail.restaurant_cusines

        tv_ins.text = orderDetail.scheduleTime
        tv_status.text = orderDetail.status

        rvOrderItems.adapter = OrderItemAdapter(orderDetail?.orderDetails!!)

        cartSubTotal.text = orderDetail.priceBeforePromo
        tv_total.text = orderDetail.finalPrice
        textView27.text = orderDetail.deliveryFee
        tv_promotion.text =
            (orderDetail.priceBeforePromo.toDouble() - orderDetail.priceAfterPromo.toDouble()).toString()

        CommonUtils.loadImage(img_rest, orderDetail.restaurantImage)

        bt_cancel.setOnClickListener {
            viewModel.cancelOrder(orderDetail.id.toString())

        }
    }
}
