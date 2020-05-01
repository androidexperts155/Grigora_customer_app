package com.rvtechnologies.grigora.view.ui.pickup_restaurants

import android.graphics.*
import android.graphics.drawable.Drawable
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_map_icon.view.*
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

        viewModel.convertPickup.observe(this, Observer {
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
            viewModel.updateType(data.id.toString(), "2", CommonUtils.getToken());
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
            mMap?.setMinZoomPreference(5f)

            val coordinate =
                LatLng(
                    latitude,
                    longitude
                )

            val location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 5f
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
                .icon(bitmapDescriptorFromVector(R.drawable.rest_home)).title("Home")


            val coordinate =
                LatLng(
                    latitude,
                    longitude
                )

            val location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 5f
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
                    .icon(bitmapDescriptorFromVector(getMarker(allRestaurants[i])))
                    .title(allRestaurants[i].name)

//                val latLng = LatLng(latitude, longitude)
//                loadMarkerImage(latLng, allRestaurants[i].image, i)


                var m = mMap.addMarker(marker)
                allRestaurants[i].markerId = m.id

            }
        }


    }

    private fun getMarker(restaurantDetailModel: NewDashboardModel.AllRestautants): Int {

        if (restaurantDetailModel.fullTime == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    restaurantDetailModel.openingTime,
                    restaurantDetailModel.closingTime
                )
            ) {
//                restaurant is closed
                return R.drawable.rest_unavailable

            }
        } else if ((CommonUtils.isRestaurantOpen(
                restaurantDetailModel.openingTime,
                restaurantDetailModel.closingTime
            ) || restaurantDetailModel.fullTime == "1") && restaurantDetailModel.busyStatus == "1"
        ) {
//restaurant is busy
            return R.drawable.rest_unavailable

        }

        return R.drawable.rest_available
    }

    private fun loadMarkerImage(latlng: LatLng, image: String, position: Int) {
        Picasso.get()
            .load(image).placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
            .into(object : com.squareup.picasso.Target {
                override
                fun onBitmapLoaded(bitmap: Bitmap?, loadedFrom: Picasso.LoadedFrom?) {
                    val marker: Marker = mMap.addMarker(
                        MarkerOptions()
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    getMarkerBitmapFromView(
                                        bitmap!!
                                    )
                                )
                            )
                            .position(latlng)
                    )
                    allRestaurants[position].markerId = marker.id
                }

                override
                fun onPrepareLoad(drawable: Drawable?) {
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }
            })

    }


    private fun getMarkerBitmapFromView(
        resId: Bitmap
    ): Bitmap? {
        var view = LayoutInflater.from(context).inflate(
            R.layout.custom_map_icon,
            null,
            false
        )


        view.profile_image.setImageBitmap(resId)
        view.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        drawable?.draw(canvas)
        view.draw(canvas)
        return getCroppedBitmap(returnedBitmap)
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
            bitmap.width / 2.toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
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
                tv_time.visibility = View.VISIBLE
                tv_tt.visibility = View.VISIBLE
                tv_pickup_desc.visibility = View.VISIBLE

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

                tv_pickup_desc.text = CommonUtils.getRoundedOff(
                    distance.toDouble()
                ) + " " + getString(
                    R.string.km_away
                )

                if (data.fullTime == "1") {
                    tv_tt.text = getString(R.string.open_24_hours)
                    tv_time.visibility = View.GONE
                } else {
                    tv_tt.text = getString(R.string.open_or_close_time)

                    tv_time.text = CommonUtils.getFormattedUtc(
                        data.openingTime,
                        "HH:mm:ss",
                        "hh:mm aa"
                    ) + " to " + CommonUtils.getFormattedUtc(
                        data.closingTime,
                        "HH:mm:ss",
                        "hh:mm aa"
                    )
                }


                var price =
                    AppConstants.base_delivery_fee.toFloat() + (distance * AppConstants.min_kilo_meter.toFloat())
                tv_delivery_charges.text =
                    "₦" + (price.toInt()).toString() + " " + tv_delivery_time.context.getString(R.string.delivery)

                handleClosed(data)
            }
        }
        return true
    }

    private fun handleClosed(restaurantDetailModel: NewDashboardModel.AllRestautants) {


        tv_status.visibility = View.GONE

        //        not always opened
        if (restaurantDetailModel.fullTime == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    restaurantDetailModel.openingTime,
                    restaurantDetailModel.closingTime
                )
            ) {
//                restaurant is closed
                tv_time.visibility = View.GONE
                tv_tt.visibility = View.GONE
                tv_status.visibility = View.VISIBLE
                tv_status.text = getString(R.string.closed)
                tv_pickup_desc.visibility = View.GONE

            }
        }
        if ((CommonUtils.isRestaurantOpen(
                restaurantDetailModel.openingTime,
                restaurantDetailModel.closingTime
            ) || restaurantDetailModel.fullTime == "1") && restaurantDetailModel.busyStatus == "1"
        ) {
//restaurant is busy
            tv_time.visibility = View.GONE
            tv_tt.visibility = View.GONE
            tv_status.visibility = View.VISIBLE

            tv_status.text = getString(R.string.busy)
            tv_pickup_desc.visibility = View.GONE

        }
    }

}


