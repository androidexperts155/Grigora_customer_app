package com.rvtechnologies.grigora.view.ui.dashboard

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.gson.Gson

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.*
import com.rvtechnologies.grigora.view_model.NewDashBoardViewModel
import com.rvtechnologies.grigorahq.network.ConnectionNetwork
import com.rvtechnologies.grigorahq.network.EventBroadcaster
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.new_dash_board_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.*

class NewDashBoardFragment : Fragment(), IRecyclerItemClick {

    private lateinit var viewModel: NewDashBoardViewModel
    var map = HashMap<String, Any>()
    lateinit var newDashboardModel: NewDashboardModel
    lateinit var dashbordadapter: DashboardAdapter

    companion object {
        fun newInstance() = NewDashBoardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewDashBoardViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { it ->
            if (it) {
                li_not_delivering.visibility = View.GONE

                shimmer_view.visibility = View.VISIBLE
                li_data.visibility = View.GONE
            } else {
                li_not_delivering.visibility = View.GONE

                shimmer_view.visibility = View.GONE
                li_data.visibility = View.VISIBLE
            }
        })

        viewModel.dashboardResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {

                newDashboardModel = response.data as NewDashboardModel
                if (newDashboardModel.allRestaurants.size == 0) {
                    li_data.visibility = View.GONE
                    li_not_delivering.visibility = View.VISIBLE

                    not_delivering.visibility = View.VISIBLE
                    went_wrong.visibility = View.GONE
                    tv_message.text = getString(R.string.not_delivering_message)

                } else {
                    li_data.visibility = View.VISIBLE
                    li_not_delivering.visibility = View.GONE

                    var temp = ArrayList<NewDashboardModel.CustomizedData>()

                    if (newDashboardModel.is_cart != null) {
                        (activity as MainActivity).showFab()
                        (activity as MainActivity).tv_restname.text =
                            newDashboardModel.is_cart.restaurantName
                        (activity as MainActivity).tv_items.text =
                            newDashboardModel.is_cart.quantity.toString() + " Items"

                        ((activity) as MainActivity).fab_cart.setOnClickListener {
                            view?.findNavController()
                                ?.navigate(
                                    R.id.action_dashBoardFragment_fragment_to_cart
                                )
                        }
                    }

                    for (i in newDashboardModel.customizedData) {
                        if (i.restaurants.isEmpty()) {
                            temp.add(i)
                        }
                    }

                    newDashboardModel.customizedData.removeAll(temp)

                    dashbordadapter = DashboardAdapter(newDashboardModel, this)
                    rc_dashboard.adapter = dashbordadapter
                }

            } else {
                li_data.visibility = View.GONE
                not_delivering.visibility = View.GONE
                li_not_delivering.visibility = View.VISIBLE
                went_wrong.visibility = View.VISIBLE
                tv_message.text = getString(R.string.oops_went_wrong)

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        return inflater.inflate(R.layout.new_dash_board_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map["latitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE)
        map["token"] = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        map["longitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        map["filter_id"] = "0"
        map["cuisine_id"] = "0"
        map["user_id"] = CommonUtils.getPrefValue(context!!, PrefConstants.ID)

        viewModel.getDashboardData(map)
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).menuAddress()
            (activity as MainActivity).updateLocation()
            (activity as MainActivity).showBottomNavigation(0)

//            (activity as MainActivity).setRightIcon(R.drawable.ic_logout)
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onItemClick(item: Any) {
        if (item is NewDashboardModel.Filter) {
            if (!item.selected)
                applyFilter("filter_id", item.id.toString())
            else
                applyFilter("filter_id", "0")
        } else if (item is NewDashboardModel.Promo) {

        } else if (item is NewDashboardModel.Cuisine) {
            if (!item.selected)
                applyFilter("cuisine_id", item.id.toString())
            else
                applyFilter("cuisine_id", "0")

        } else if (item is NewDashboardModel.CustomizedData.Restaurant) {

            when (item.uiTpe) {
                "1" -> {
//                    RESTAURANT
                    val bundle = bundleOf(AppConstants.RESTAURANT_ID to item.id)
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_dashBoardFragment_fragment_to_restaurantDetails,
                            bundle
                        )

                }
                "2" -> {
                    //                    TOP BRAND


                }
                "3" -> {
                    //                    CUISINE


                }
            }


//            if (newDashboardModel.customizedData[position - topSize].uiType.equals("1"))
//                RESTAURANTS_HORIZONTAL
//            else if (newDashboardModel.customizedData[position - topSize].uiType.equals("2"))
//                CUISINES
//            else
//                TOP_BRANDS
        } else if (item is NewDashboardModel.AllRestautants) {

        }
    }

    fun applyFilter(key: String, value: String) {
        map[key] = value
        viewModel.getDashboardData(map)
    }
}
