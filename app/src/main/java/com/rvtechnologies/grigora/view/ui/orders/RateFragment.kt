package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RateDriverFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import kotlinx.android.synthetic.main.rate_driver_fragment.*

class RateFragment : Fragment() {

    companion object {
        fun newInstance() = RateFragment()
    }

    private lateinit var viewModel: RateViewModel
    private var orderItemModel: OrderItemModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RateViewModel::class.java)
        if (arguments != null) {
            orderItemModel = arguments?.getParcelable<OrderItemModel>(AppConstants.ORDER_ITEM_MODEL)
            viewModel.isDriver.value = arguments?.getBoolean(AppConstants.IS_DRIVER)!!

            var rateText = ""
            var image = ""

            rateText = if (viewModel.isDriver.value!!) String.format(
                getString(R.string.driver_text),
                orderItemModel?.driverName
            ) else String.format(
                getString(R.string.restaurant_text),
                orderItemModel?.restaurantName
            )

            image = if (viewModel.isDriver.value!!) String.format(
                getString(R.string.driver_text),
                orderItemModel?.driverImage
            ) else String.format(
                getString(R.string.restaurant_text),
                orderItemModel?.restaurantImage
            )

            viewModel.orderItemModel.value = orderItemModel
            viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
            viewModel.rateText.value = rateText
            viewModel.image.value = image
        }

        viewModel.rateResult.observe(this, Observer { rateRes ->
            if (rateRes is CommonResponseModel<*>) {
                rateRes.message?.let { CommonUtils.showMessage(parentView, it) }
                if (!viewModel.isDriver.value!!) {
                    val bundle = bundleOf(
                        AppConstants.IS_DRIVER to true,
                        AppConstants.ORDER_ITEM_MODEL to orderItemModel
                    )
                    view?.findNavController()
                        ?.navigate(R.id.action_rateDriverFragment_self, bundle)
                } else {
                    val bundle = bundleOf(
                         AppConstants.ORDER_ITEM_MODEL to orderItemModel
                    )
                    view?.findNavController()
                        ?.navigate(R.id.action_rateDriverFragment_to_rateDishFragment, bundle)


//
//                    view?.findNavController()?.popBackStack()
//                    view?.findNavController()?.popBackStack()
                }

            }
        })

        viewModel.valid.observe(this, Observer {
            CommonUtils.showMessage(parentView, getString(R.string.default_notification_channel_id))
        })

        viewModel.isLoading.observe(this, Observer { loading ->
            if (loading) {

                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rateDriverFragmentBinding = DataBindingUtil.inflate<RateDriverFragmentBinding>(
            inflater,
            R.layout.rate_driver_fragment,
            container,
            false
        )
        rateDriverFragmentBinding.rateViewModel = viewModel
        return rateDriverFragmentBinding.root
    }

    fun back() {
        activity?.onBackPressed()
    }


}
