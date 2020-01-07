package com.rvtechnologies.grigora.view.ui.addresses

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.SelectLocationFragmentBinding
import com.rvtechnologies.grigora.utils.AddressUtils
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.select_location_fragment.*
import java.net.URISyntaxException


class SelectLocationFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() =
            SelectLocationFragment()
    }

    private lateinit var mMap: GoogleMap

    private var latitude = 0.0
    private var longitude = 0.0
    private var address = ""

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        try {
            updateMap()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private lateinit var selectLocationViewBinding: SelectLocationFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            selectLocationViewBinding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                R.layout.select_location_fragment,
                container,
                false
            ) as SelectLocationFragmentBinding
            selectLocationViewBinding.selectLocationView = this
            return selectLocationViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
        if (!CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).isNullOrEmpty()) {
            latitude = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble()
            longitude = CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
            address = CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
        }



        if (latitude > 0.0 &&
            longitude > 0.0
        ) {
            view.findNavController()
                .navigate(R.id.action_selectLocationFragment_to_navigationRestaurants)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    fun selectCity() {
        if (!Places.isInitialized()) {
            context?.let { Places.initialize(it, resources.getString(R.string.google_api_key)) }
        }
        val fields: List<Place.Field?>
        fields = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME)
        var intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setTypeFilter(TypeFilter.CITIES).build(activity!!)
        startActivityForResult(intent, AppConstants.SELECT_CITY)
    }

    fun selectArea() {
        if (!Places.isInitialized()) {
            context?.let { Places.initialize(it, resources.getString(R.string.google_api_key)) }
        }
        val fields: List<Place.Field?>
        fields = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)

        var intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setTypeFilter(TypeFilter.ADDRESS)
            .build(activity!!)
        startActivityForResult(intent, AppConstants.SELECT_AREA)
    }

    fun useCurrentLocation() {
        Dexter.withActivity(activity)
            .withPermission(permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getCurrentLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {

                }
            }).check()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        activity?.getString(R.string.loading_getting_location)?.let {
            CommonUtils.showLoader(
                activity!!,
                it
            )
        }
        var mLocation: Location?

        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)

        for (i in providers.size - 1 downTo 0) {
            mLocation = lm.getLastKnownLocation(providers[i])
            if (mLocation != null) {
                latitude = mLocation.latitude
                longitude = mLocation.longitude
                updateMap()
                activity?.let {
                    AddressUtils.getAddressFromLocation(latitude.toDouble(), longitude.toDouble(),
                        it, Handler {
                            address = it.data?.get("address").toString()
                            saveLocationContinue()
                            return@Handler true
                        })
                }
                break
            }

        }
    }

    fun saveLocationContinue() {
        CommonUtils.hideLoader()
        if (latitude > 0.0 &&
            longitude > 0.0
        ) {
            CommonUtils.savePrefs(context, PrefConstants.LATITUDE, latitude.toString())
            CommonUtils.savePrefs(context, PrefConstants.LONGITUDE, longitude.toString())
            CommonUtils.savePrefs(context, PrefConstants.ADDRESS, address)
            (activity as MainActivity).updateLocation()

//            clear stack before proceeding further as whole new thread start here
            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .popBackStack(R.id.nav_graph_xml, true)

            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .navigate(R.id.locationTypeFragment)
        } else {
            CommonUtils.showMessage(parentView, getString(R.string.error_location))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.SELECT_CITY) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                latitude = place.latLng?.latitude!!
                longitude = place.latLng?.longitude!!
            }
            if (requestCode == AppConstants.SELECT_AREA) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                latitude = place.latLng?.latitude!!
                longitude = place.latLng?.longitude!!
                address = place.address.toString()
                txtAddress.text = address
            }
            updateMap()
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data!!)
            status.statusMessage?.let { CommonUtils.showMessage(parentView, it) }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()

//        restore old lat lng as user has aborted select location
        if (latitude == 0.0 &&
            longitude == 0.0
        ) {
            CommonUtils.savePrefs(
                activity,
                PrefConstants.LATITUDE,
                CommonUtils.getPrefValue(activity, PrefConstants.TEMP_LATITUDE)
            )
            CommonUtils.savePrefs(
                activity,
                PrefConstants.LONGITUDE,
                CommonUtils.getPrefValue(activity, PrefConstants.TEMP_LONGITUDE)
            )
            CommonUtils.savePrefs(
                activity,
                PrefConstants.ADDRESS,
                CommonUtils.getPrefValue(activity, PrefConstants.TEMP_ADDRESS)
            )


            CommonUtils.savePrefs(activity, PrefConstants.TEMP_ADDRESS, "")
            CommonUtils.savePrefs(activity, PrefConstants.TEMP_LONGITUDE, "")
            CommonUtils.savePrefs(activity, PrefConstants.TEMP_LATITUDE, "")

            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .popBackStack(R.id.nav_graph_xml, true)

            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .navigate(R.id.navigationRestaurants)
        }
    }


    fun updateMap() {
        mMap.clear()
        val marker = MarkerOptions().position(LatLng(latitude, longitude))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_icon)).title("Driver")
        mMap.addMarker(marker)
        if (marker == null) {
            return
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
        mMap?.setMinZoomPreference(12f)


    }


}
