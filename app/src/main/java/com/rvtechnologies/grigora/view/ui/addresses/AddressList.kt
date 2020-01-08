package com.rvtechnologies.grigora.view.ui.addresses

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.Navigation

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.AddressListFragmentBinding
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.addresses.adapter.AddressesAdapter
import com.rvtechnologies.grigora.view_model.AddressListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.img_back
import kotlinx.android.synthetic.main.address_list_fragment.*

class AddressList : Fragment(), IRecyclerItemClick {
    private lateinit var addressListFragmentBinding: AddressListFragmentBinding
    private var addressesList = ArrayList<AddressModel>()
    private var adapter: AddressesAdapter? = null
    private lateinit var viewModel: AddressListViewModel

    companion object {
        fun newInstance() = AddressList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddressListViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(this.context, PrefConstants.TOKEN)
        viewModel.addressesResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                addressesList.clear()
                addressesList.addAll(response.data as Collection<AddressModel>)
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
        addressListFragmentBinding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.address_list_fragment,
            container,
            false
        ) as AddressListFragmentBinding
        addressListFragmentBinding.addressListViewModel = viewModel
        addressListFragmentBinding.addressListfragment = this
        return addressListFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AddressesAdapter(addressesList, this)
        rc_addresses.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAddresses()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    fun back() {
        Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
            .popBackStack()
    }

    fun addClicked() {
        //            save in temp values before clearing so that if we press back at choose location, we can have previous location stored. This functionality is done because in select location fragment in onCreate user is aitomatically redirected to dashboard if there is location
//        CommonUtils.savePrefs(
//            this.context,
//            PrefConstants.TEMP_LATITUDE,
//            CommonUtils.getPrefValue(this.context, PrefConstants.LATITUDE)
//        )
//        CommonUtils.savePrefs(
//            this.context,
//            PrefConstants.TEMP_LONGITUDE,
//            CommonUtils.getPrefValue(this.context, PrefConstants.LONGITUDE)
//        )
//        CommonUtils.savePrefs(
//            this.context,
//            PrefConstants.TEMP_ADDRESS,
//            CommonUtils.getPrefValue(this.context, PrefConstants.ADDRESS)
//        )
//
//        CommonUtils.savePrefs(this.context, PrefConstants.LATITUDE, "")
//        CommonUtils.savePrefs(this.context, PrefConstants.LONGITUDE, "")
//        CommonUtils.savePrefs(this.context, PrefConstants.ADDRESS, "")

        Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
            .navigate(R.id.action_addressListFragment_to_selectLocationFragment)

    }

    override fun onItemClick(item: Any) {
        item as AddressModel
        CommonUtils.savePrefs(context, PrefConstants.LATITUDE, item.latitude)
        CommonUtils.savePrefs(context, PrefConstants.LONGITUDE, item.longitude)
        CommonUtils.savePrefs(context, PrefConstants.ADDRESS, item.address)
        CommonUtils.savePrefs(context, PrefConstants.COMPLETE_ADDRESS, item.completeAddress)
        CommonUtils.savePrefs(context, PrefConstants.ADDRESS_ID, item.id.toString())

        Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
            .popBackStack(R.id.nav_graph_xml, true)

        Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
            .navigate(R.id.navigationRestaurants)
    }
}
