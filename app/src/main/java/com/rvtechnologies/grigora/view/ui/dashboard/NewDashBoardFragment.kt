package com.rvtechnologies.grigora.view.ui.dashboard

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.FilterModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.FilterAdapter
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.OfferAdapter
import com.rvtechnologies.grigora.view_model.NewDashBoardViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_dash_board_fragment.*

class NewDashBoardFragment : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = NewDashBoardFragment()
    }

    private lateinit var viewModel: NewDashBoardViewModel
    private var filterList = ArrayList<FilterModel>()
    lateinit var filterAdapter: FilterAdapter

    private var offerList = ArrayList<String>()
    lateinit var offerAdapter: OfferAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewDashBoardViewModel::class.java)
        filterAdapter = FilterAdapter(filterList, this)
        offerAdapter = OfferAdapter(offerList, this)
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
        filterList.add(FilterModel("Rating", true, false))
        filterList.add(FilterModel("Vegeterian", false, true))
        filterList.add(FilterModel("Non-Veg", false, false))
        filterList.add(FilterModel("North Indian", false, false))
        filterList.add(FilterModel("Sea Food", false, false))
        filterList.add(FilterModel("Desserts", false, false))
        filterList.add(FilterModel("Healthy", false, false))

        offerList.add("")
        offerList.add("")
        offerList.add("")
        offerList.add("")
        offerList.add("")
        offerList.add("")
        offerList.add("")
        offerList.add("")

        rc_filter.adapter = filterAdapter
        rc_offers.adapter = offerAdapter
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).updateLocation()
            (activity as MainActivity).lockDrawer(false)

        }
    }

    override fun onItemClick(item: Any) {
    }


}
