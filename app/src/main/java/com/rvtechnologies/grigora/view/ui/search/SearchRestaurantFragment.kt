package com.rvtechnologies.grigora.view.ui.search

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.SearchRestaurantFragmentBinding
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantAdapter
import com.rvtechnologies.grigora.view.ui.search.adapter.SearchRestaurantAdapter
import com.rvtechnologies.grigora.view_model.SearchRestaurantViewModel
import kotlinx.android.synthetic.main.search_restaurant_fragment.*

class SearchRestaurantFragment : Fragment(), IRecyclerItemClick {
    var filter = "1"

    companion object {
        fun newInstance() = SearchRestaurantFragment()
    }

    private lateinit var viewModel: SearchRestaurantViewModel
    private val restaurantList = ArrayList<NewDashboardModel.AllRestautants>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchRestaurantViewModel::class.java)

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
                        rec_search.adapter = SearchRestaurantAdapter(restaurantList, this)
                        rec_search.adapter?.notifyDataSetChanged()
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

        viewModel?.getRestaurants(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
            "",
            filter
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var searchRestaurantFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.search_restaurant_fragment,
            container,
            false
        ) as SearchRestaurantFragmentBinding

        searchRestaurantFragmentBinding.searchRestaurantView = this
        searchRestaurantFragmentBinding.searchRestaurantViewModel = viewModel
        return searchRestaurantFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ed_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 2) {
                    viewModel?.getRestaurants(
                        CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                        ed_search.text.toString(),
                        filter
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(
            CommonUtils.getPrefValue(
                context!!,
                PrefConstants.ADDRESS
            )
        )
    }


    fun all() {
        filter = "1"
        viewModel?.getRestaurants(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
            ed_search.text.toString(),
            filter
        )
        tv_all.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_selected_bg)
        tv_all.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.white
            )
        )

        tv_restaurants.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)

        tv_cuisines.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)


        if (CommonUtils.isDarkMode()) {
            tv_restaurants.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )

            tv_cuisines.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )
        } else
            tv_restaurants.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.textBlack
                )
            )

        tv_cuisines.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.textBlack
            )
        )

    }

    fun cuisines() {
        filter = "3"
        viewModel?.getRestaurants(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
            ed_search.text.toString(),
            filter
        )
        tv_cuisines.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_selected_bg)
        tv_cuisines.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.white
            )
        )

        tv_restaurants.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)

        tv_all.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)


        if (CommonUtils.isDarkMode()) {
            tv_restaurants.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )

            tv_all.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )
        } else
            tv_restaurants.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.textBlack
                )
            )

        tv_all.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.textBlack
            )
        )
    }

    fun restaurants() {
        filter = "2"
        viewModel?.getRestaurants(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
            ed_search.text.toString(),
            filter
        )
        tv_restaurants.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_selected_bg)
        tv_restaurants.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.white
            )
        )

        tv_cuisines.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)
        tv_all.background =
            ContextCompat.getDrawable(context!!, R.drawable.chip_deselected_bg)



        if (CommonUtils.isDarkMode()) {
            tv_cuisines.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )

            tv_all.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.white
                )
            )
        } else
            tv_cuisines.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.textBlack
                )
            )

        tv_all.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.textBlack
            )
        )
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
                ?.navigate(R.id.searchRestaurants_to_restaurant_detail, bundle)
        }
    }
}
