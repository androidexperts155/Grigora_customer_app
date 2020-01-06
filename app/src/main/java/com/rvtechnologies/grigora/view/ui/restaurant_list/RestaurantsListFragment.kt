package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentRestaurantsListBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.OnItemClickListener
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantAdapter
import com.rvtechnologies.grigora.view_model.RestaurantsListFragmentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_filter.*
import kotlinx.android.synthetic.main.fragment_restaurants_list.*

class RestaurantsListFragment : Fragment(), OnItemClickListener {
    override fun onItemClick(item: Any) {
        if (item is RestaurantModel) {
            val bundle = bundleOf(AppConstants.RESTAURANT_MODEL to item)
            view?.findNavController()
                ?.navigate(R.id.action_restaurantsListFragment_to_restaurantDetailsFragment, bundle)
        }
    }

    fun filterClicked() {
        val layoutInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View
        if (layoutInflater == null) {
            return
        }
        popupView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val popupWindow = PopupWindow(context)
        popupWindow.contentView = popupView
        popupWindow.width = CommonUtils.dip2pixel(context!!, 200f)
        popupWindow.height = ListPopupWindow.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(context!!.resources.getDrawable(android.R.color.transparent))
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupWindow.setOnDismissListener { view_overlay.visibility = View.GONE }

        when {
            viewModel?.sort?.value.equals("1") -> {
                popupView.findViewById<RadioButton>(R.id.rd_nearby).visibility = VISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_ratings).visibility = INVISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_all).visibility = INVISIBLE
            }
            viewModel?.sort?.value.equals("2") -> {
                popupView.findViewById<RadioButton>(R.id.rd_nearby).visibility = INVISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_ratings).visibility = VISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_all).visibility = INVISIBLE
            }
            viewModel?.sort?.value.equals("3") -> {
                popupView.findViewById<RadioButton>(R.id.rd_nearby).visibility = INVISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_ratings).visibility = INVISIBLE
                popupView.findViewById<RadioButton>(R.id.rd_all).visibility = VISIBLE
            }
        }
        popupView.findViewById<RelativeLayout>(R.id.rel_nearby).setOnClickListener {
            sort("1")
            popupWindow.dismiss()
        }

        popupView.findViewById<RelativeLayout>(R.id.rel_ratings).setOnClickListener {
            sort("2")
            popupWindow.dismiss()
        }
        popupView.findViewById<RelativeLayout>(R.id.rel_all).setOnClickListener {
            sort("3")
            popupWindow.dismiss()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupWindow.showAsDropDown(
                img_filter,
                img_filter.getRight() - popupWindow.width - img_filter.getWidth() / 2,
                0,
                Gravity.CENTER or Gravity.TOP
            )
        } else {
            popupWindow.showAsDropDown(
                img_filter,
                img_filter.getRight() - popupWindow.width - img_filter.getWidth() / 2,
                0
            )
        }

        view_overlay.visibility = VISIBLE
    }

    private var viewModel: RestaurantsListFragmentViewModel? = null
    private val restaurantList = ArrayList<RestaurantModel>()
    private var fragmentRestaurantsListBinding: FragmentRestaurantsListBinding? = null
    private var searchText: String = ""

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
                        restaurantList.addAll(response.data as Collection<RestaurantModel>)
                        rvRestaurants.adapter?.notifyDataSetChanged()
                        noRestaurant.visibility = if (restaurantList.isEmpty()) VISIBLE else GONE
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

        viewModel?.getRestaurants("", "0");


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRestaurants.adapter = RestaurantAdapter(restaurantList, this)

        rvRestaurants.isNestedScrollingEnabled = false

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text?.isNotBlank()!!)
                    searchText = text.toString()
                if (CommonUtils.getPrefValue(activity, PrefConstants.ID).isNullOrEmpty()) {
                    viewModel?.getRestaurants(searchText, "0")
                } else {
                    viewModel?.getRestaurants(
                        searchText,
                        CommonUtils.getPrefValue(activity, PrefConstants.ID)
                    )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        img_filter.setOnClickListener {
            filterClicked()
        }
    }


    fun sort(id: String) {
        viewModel?.sort?.value = id

        fragmentRestaurantsListBinding?.restaurantListViewModel = viewModel
        if (CommonUtils.getPrefValue(activity, PrefConstants.ID).isNullOrEmpty()) {
            viewModel?.getRestaurants(searchText, "0")
        } else {
            viewModel?.getRestaurants(
                searchText,
                CommonUtils.getPrefValue(activity, PrefConstants.ID)
            )

        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.VISIBLE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).lockDrawer(false)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            RestaurantsListFragment()
    }
}
