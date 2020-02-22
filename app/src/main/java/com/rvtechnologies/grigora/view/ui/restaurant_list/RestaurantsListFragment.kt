package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentRestaurantsListBinding
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantAdapter
import com.rvtechnologies.grigora.view_model.RestaurantsListFragmentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_restaurants_list.*

class RestaurantsListFragment : Fragment(), IRecyclerItemClick {

    companion object {
        @JvmStatic
        fun newInstance() =
            RestaurantsListFragment()
    }

    private var viewModel: RestaurantsListFragmentViewModel? = null
    private val restaurantList = ArrayList<NewDashboardModel.AllRestautants>()
    private var fragmentRestaurantsListBinding: FragmentRestaurantsListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this).get(RestaurantsListFragmentViewModel::class.java)
        viewModel?.lat?.value = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble()
        viewModel?.lng?.value =
            CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()

        viewModel?.restaurantListRes?.observe(this,
            Observer { response ->
                restaurantList.clear()

                if (response is CommonResponseModel<*>) {

                    if (response.status!!) {
                        var model =
                            response.data as PickupRestaurantsModel

                        restaurantList.addAll(model.mainInfo)
                        tv_count.text="${restaurantList.size} ${getString(R.string.places)}"
                        rc_addresses.adapter= RestaurantAdapter(restaurantList,this)
                        rc_addresses.adapter?.notifyDataSetChanged()
                     }
                } else {
                    CommonUtils.showMessage(parentView, response.toString())
                }
            })
        viewModel?.isLoading?.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRestaurantsListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_restaurants_list, container, false
        )
        fragmentRestaurantsListBinding?.restaurantListFragment = this
        fragmentRestaurantsListBinding?.restaurantListViewModel = viewModel

        return fragmentRestaurantsListBinding?.root
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).backTitle(getString(R.string.popular))
            (activity as MainActivity).showBottomNavigation(2)
            (activity as MainActivity).img_back.visibility = View.GONE
        }
        viewModel?.getRestaurants(CommonUtils.getPrefValue(context!!,PrefConstants.TOKEN))

    }

    override fun onItemClick(item: Any) {
        if (item is NewDashboardModel.AllRestautants) {
            val bundle = bundleOf(
                AppConstants.RESTAURANT_ID to item.id,
                AppConstants.RESTAURANT_PICKUP to item.pickup,
                AppConstants.RESTAURANT_BOOKING to item.table_booking,
                AppConstants.RESTAURANT_SEATES to item.no_of_seats,
                AppConstants.RESTAURANT_CLOSING_TIME to item.closingTime,
                AppConstants.RESTAURANT_OPENING_TIME to item.openingTime,
                AppConstants.RESTAURANT_ALWAYS_OPEN to item.fullTime,
                AppConstants.FROM_PICKUP to false

            )

            view?.findNavController()
                ?.navigate(R.id.action_restaurantsListFragment_to_restaurantDetailsParentFragment, bundle)
        }
    }

}
