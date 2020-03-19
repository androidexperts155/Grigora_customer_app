package com.rvtechnologies.grigora.view.ui.pickup_restaurants

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.ImagesAdapter
import com.rvtechnologies.grigora.view_model.PickupRestaurantsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pickup_restaurants_fragment.*


class PickupRestaurants : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    IRecyclerItemClick {
    override fun onItemClick(item: Any) {
    }

    var allRestaurants = ArrayList<NewDashboardModel.AllRestautants>()
    private lateinit var mMap: GoogleMap
    lateinit var data: NewDashboardModel.AllRestautants

    companion object {
        fun newInstance() = PickupRestaurants()
    }

    private lateinit var viewModel: PickupRestaurantsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PickupRestaurantsViewModel::class.java)

        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

        viewModel.restaurantsResponse.observe(this, Observer {


            var model = (it as CommonResponseModel<*>).data as PickupRestaurantsModel
            var temp = ArrayList<NewDashboardModel.AllRestautants>()
            temp.addAll(model.mainInfo)
            allRestaurants.clear()
            for (data in temp) {
                if (data.items != null && data.items.size > 0) {
                    allRestaurants.add(data)
                }
            }
            mMap.clear()
            updateMap()


        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pickup_restaurants_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mMap) as SupportMapFragment

        mapFragment.getMapAsync(this)


        viewModel.getRestaurants(
            CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE),
            CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        )


        img_next.setOnClickListener {


            val bundle = bundleOf(
                AppConstants.RESTAURANT_ID to data.id,
                AppConstants.RESTAURANT_PICKUP to data.pickup,
                AppConstants.RESTAURANT_BOOKING to data.table_booking,
                AppConstants.RESTAURANT_SEATES to data.no_of_seats,
                AppConstants.RESTAURANT_CLOSING_TIME to data.closingTime,
                AppConstants.RESTAURANT_OPENING_TIME to data.openingTime,
                AppConstants.RESTAURANT_ALWAYS_OPEN to data.fullTime,
                AppConstants.FROM_PICKUP to true
            )
            view?.findNavController()
                ?.navigate(
                    R.id.action_pickupRestaurants_to_rest_parent,
                    bundle
                )
        }

        card_search.setOnClickListener {
            viewModel.getRestaurants(
                CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE),
                CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
            )
        }
        bt_cursor.setOnClickListener {
            var latitude = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE).toDouble()
            var longitude = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE).toDouble()

            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
            mMap?.setMinZoomPreference(15f)

            val coordinate =
                LatLng(
                    latitude,
                    longitude
                )

            val location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15f
            )
            mMap.animateCamera(location)

        }

        img_close.setOnClickListener {
            card_res.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.pickup))
        (activity as MainActivity).showBottomNavigation(1)
        (activity as MainActivity).img_back.visibility = View.GONE

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener(this)
        if (CommonUtils.isDarkMode())
            googleMap.setMapStyle(
                MapStyleOptions(
                    getResources()
                        .getString(R.string.dark_mode_style)
                )
            )
        else
            googleMap.setMapStyle(
                MapStyleOptions(
                    getResources()
                        .getString(R.string.light_mode_style)
                )
            )


    }

    private fun updateMap() {
        if (::mMap.isInitialized) {
            var latitude = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE).toDouble()
            var longitude = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE).toDouble()

            mMap.clear()

            val marker = MarkerOptions().position(LatLng(latitude, longitude))
                .icon(bitmapDescriptorFromVector(R.drawable.ic_home_run)).title("Home")


            val coordinate =
                LatLng(
                    latitude,
                    longitude
                )

            val location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15f
            )
            mMap.addMarker(marker)
            mMap.animateCamera(location)


            if (marker == null) {
                return
            }

//            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
//            mMap?.setMinZoomPreference(15f)


            for (i in 0 until allRestaurants.size) {
                var latitude =
                    allRestaurants[i].latitude.toDouble()
                var longitude =
                    allRestaurants[i].longitude.toDouble()


                val marker = MarkerOptions().position(LatLng(latitude, longitude))
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_rest))
                    .title(allRestaurants[i].name)

                var m = mMap.addMarker(marker)
                allRestaurants[i].markerId = m.id

            }
        }


    }

    private fun bitmapDescriptorFromVector(
        vectorResId: Int
    ): BitmapDescriptor? {
        val vectorDrawable =
            ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        for (detail in allRestaurants) {
            if (detail.markerId == p0!!.id) {
                data = detail

                card_res.visibility = View.VISIBLE
                tv_name.text = detail.name

                if (detail.items.isNotEmpty()) {
                    rc_images.adapter = ImagesAdapter(detail, this)
                    rc_images.setOnClickListener {
                        //                        iRecyclerItemClick.onItemClick(list[position])
                    }
                }

                tv_rating.text = detail.averageRating.toString()
                tv_delivery_time.text =
                    detail.preparing_time + " " + tv_delivery_time.context.getString(R.string.min)

                var distance = CommonUtils.calculateDistance(
                    detail.latitude.toDouble(),
                    detail.longitude.toDouble(),
                    CommonUtils.getPrefValue(tv_name.context, PrefConstants.LATITUDE).toDouble(),
                    CommonUtils.getPrefValue(tv_name.context, PrefConstants.LONGITUDE).toDouble()
                )

                var price =
                    AppConstants.base_delivery_fee.toFloat() + (distance * AppConstants.min_kilo_meter.toFloat())
                tv_delivery_charges.text =
                    "₦" + (price.toInt()).toString() + " " + tv_delivery_time.context.getString(R.string.delivery)
            }
        }
        return true
    }
}


