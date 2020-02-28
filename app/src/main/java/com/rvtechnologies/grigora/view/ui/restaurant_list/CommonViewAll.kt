package com.rvtechnologies.grigora.view.ui.restaurant_list

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
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantAdapter
import com.rvtechnologies.grigora.view_model.CommonViewAllViewModel
import kotlinx.android.synthetic.main.common_view_all_fragment.*

class CommonViewAll : Fragment(), IRecyclerItemClick {

//    filter_type(1:Your Favourite, 2:New in Grigora, 3:Order Again, 4:Popular, 5:Near By, 121:Top Cuisine), latitude, longitude

    private val restaurantList = ArrayList<NewDashboardModel.AllRestautants>()

    private lateinit var viewModel: CommonViewAllViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CommonViewAllViewModel::class.java)

        viewModel?.isLoading?.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

        viewModel.restaurantsResponse.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {

                if (response.status!!) {
                    var model =
                        response.data as PickupRestaurantsModel

                    restaurantList.addAll(model.mainInfo)
                    rec_rest.adapter = RestaurantAdapter(restaurantList, this)
                    rec_rest.adapter?.notifyDataSetChanged()
                }
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })


        viewModel.token.value=CommonUtils.getPrefValue(context!!,PrefConstants.TOKEN)
        viewModel.lat.value=CommonUtils.getPrefValue(context!!,PrefConstants.LATITUDE)
        viewModel.lng.value=CommonUtils.getPrefValue(context!!,PrefConstants.LONGITUDE)

        if(arguments?.getString(AppConstants.FILTER_ID).toString()!="121"){
            viewModel.getFilterData(arguments?.getString(AppConstants.FILTER_ID)!!)
        }
        else
        {
            viewModel.getFilterData(arguments?.getString(AppConstants.FILTER_ID)!!,arguments?.getString(AppConstants.CUISINE_ID)!!)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.common_view_all_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)

        when (arguments?.getString(AppConstants.FILTER_ID)) {
            "1" -> (activity as MainActivity).backTitle(getString(R.string.your_favourite))
            "2" -> (activity as MainActivity).backTitle(getString(R.string.new_in_grigora))
            "3" -> (activity as MainActivity).backTitle(getString(R.string.order_again))
            "4" -> (activity as MainActivity).backTitle(getString(R.string.popular))
            "5" -> (activity as MainActivity).backTitle(getString(R.string.near_by))
            "121" -> (activity as MainActivity).backTitle(getString(R.string.top_cuisine))
        }


        (activity as MainActivity).updateCartButton()

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
                ?.navigate(R.id.action_commonViewAll_to_restaurantDetailsParentFragment, bundle)
        }
    }

}
