package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RateDishFragmentBinding
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.view_model.RateDishViewModel

class RateDishFragment : Fragment() {

    companion object {
        fun newInstance() =
            RateDishFragment()
    }

    private lateinit var viewModel: RateDishViewModel
    private var orderItemModel: OrderItemModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RateDishViewModel::class.java)
        if (arguments != null) {
            orderItemModel = arguments?.getParcelable(AppConstants.ORDER_ITEM_MODEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ratedishFragmentBinding: RateDishFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.rate_dish_fragment, container, false
        )
        ratedishFragmentBinding.rateViewModel = viewModel
        ratedishFragmentBinding.rateDishView = this
        return ratedishFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}
