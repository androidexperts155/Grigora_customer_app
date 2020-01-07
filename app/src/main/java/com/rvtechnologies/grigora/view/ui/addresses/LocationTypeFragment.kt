package com.rvtechnologies.grigora.view.ui.addresses

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.LocationTypeFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.addresses.adapter.LocationTypeAdapter
import com.rvtechnologies.grigora.view_model.LocationTypeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.location_type_fragment.*

class LocationTypeFragment : Fragment(), IRecyclerItemClick {
    override fun onItemClick(item: Any) {
        if (item is LocationTypeModel) {
            CommonUtils.savePrefs(context, PrefConstants.LOCATION_TYPE, item.name)
            view?.findNavController()
                ?.navigate(R.id.action_locationTypeFragment_to_navigationRestaurants)
        }
    }

    companion object {
        fun newInstance() =
            LocationTypeFragment()
    }

    private lateinit var viewModel: LocationTypeViewModel
    private var locationTypeList = ArrayList<LocationTypeModel>()
    private var adapter: LocationTypeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LocationTypeViewModel::class.java)

        viewModel.locationTypeData.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                locationTypeList.clear()
                locationTypeList.addAll(response.data as Collection<LocationTypeModel>)
                adapter?.notifyDataSetChanged()
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val locationTypeFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.location_type_fragment,
            container,
            false
        ) as LocationTypeFragmentBinding
        locationTypeFragmentBinding.locationTypeViewModel = viewModel
        return locationTypeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLocationTypeList()

        adapter =
            LocationTypeAdapter(
                locationTypeList,
                this
            )
        rvLocationType.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
        }
    }


}
