package com.rvtechnologies.grigora.view.ui.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.GroupOrdersModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.FaqAdapter
import com.rvtechnologies.grigora.view.ui.profile.adapter.GroupOrdersAdapter
import com.rvtechnologies.grigora.view_model.GroupOrdersViewModel
import kotlinx.android.synthetic.main.group_orders_fragment.*

class GroupOrders : Fragment(), IRecyclerItemClick {
    companion object {
        fun newInstance() = GroupOrders()
    }

    private lateinit var viewModel: GroupOrdersViewModel

    var list = ArrayList<GroupOrdersModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GroupOrdersViewModel::class.java)
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, "") }
            } else {
                CommonUtils.hideLoader()
            }
        })
        viewModel.groupOrdersRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    list.clear()
                    list.addAll(response.data as Collection<GroupOrdersModel>)
                    rc_orders.adapter = GroupOrdersAdapter(list, this)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.group_orders_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.group_orders))
        viewModel.getGroupOrders(
            CommonUtils.getPrefValue(
                context!!,
                PrefConstants.TOKEN
            )
        )
    }

    override fun onItemClick(item: Any) {
        item as Int

        val bundle = bundleOf(
            AppConstants.RESTAURANT_ID to list[item].restaurant_id.toString(),
            AppConstants.CART_ID to list[item].id.toString(),
            AppConstants.FROM_PICKUP to false
        )



        view?.findNavController()
            ?.navigate(R.id.action_groupOrders_to_restaurantDetailGroup, bundle)


    }

}
