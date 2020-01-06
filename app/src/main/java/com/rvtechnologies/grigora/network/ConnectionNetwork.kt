package com.rvtechnologies.grigorahq.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.network.ApiClient
import com.rvtechnologies.grigora.utils.ApiConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.CommonUtils.hideLoader
import com.rvtechnologies.grigora.utils.CommonUtils.showLoader
import com.rvtechnologies.grigora.utils.GrigoraApp

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class ConnectionNetwork {


    companion object {

        fun checkConnection(context: Context): Boolean {
            return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
        }

        //Show snack bar
        fun showSnack(isConnected: Boolean, activity: Context, view: View, message: String) {
            var snackbar: Snackbar? = null
            try {
                if (!isConnected) {

                    //            color = Color.parseColor("#E12035");
                    snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    snackbar!!.show()
                } else {
                    snackbar!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun convertRequestBodyfromMap(
            context: Context,
            map: Map<String, Any>
        ): Map<String, RequestBody> {
            val mapData = HashMap<String, RequestBody>()
            val size = map.size
            for (i in 0 until size) {
                try {
                    val key = map.keys.toTypedArray()[i]
                    val value =
                        RequestBody.create(MediaType.parse("text/plain"), map[key].toString()!!)
                    mapData[key] = value
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            return mapData
        }

        fun converRequestBodyFromMapImage(
            map: Map<String, File>,
            type: ArrayList<String>
        ): ArrayList<MultipartBody.Part> {
            val map1 = ArrayList<MultipartBody.Part>()
            val size = map.size
            var requestBody: RequestBody? = null
            for (i in 0 until size) {
                try {
                    val key = map.keys.toTypedArray()[i]
                    when (type[i]) {
                        "1" -> requestBody =
                            RequestBody.create(MediaType.parse("video/mp4"), map[key]!!)

                        "2" -> requestBody =
                            RequestBody.create(MediaType.parse("image/*"), map[key]!!)

                        "3" -> requestBody =
                            RequestBody.create(MediaType.parse("audio/m4a"), map[key]!!)

                        "4" -> requestBody =
                            RequestBody.create(MediaType.parse("audio/m4a"), map[key]!!)
                    }

                    val body =
                        MultipartBody.Part.createFormData(key, map[key]!!.name, requestBody!!)
                    map1.add(body)

                } catch (ignored: IndexOutOfBoundsException) {
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            }
            return map1
        }


        fun getData(
            url: String,
            header: Map<String, String>,
            fields: Map<String, RequestBody>,
            file: ArrayList<MultipartBody.Part>,
            file1: ArrayList<MultipartBody.Part>,
            context: Context,
            viewGroup: ViewGroup,
            key: Int
        ) {
            showLoader(context, context.getString(R.string.loading))
            var fullUrl = ApiConstants.BASE_URL + url;
            if (checkConnection(context)) {
                var call: Call<JsonElement> =
                    ApiClient.getApiServices().postData(fullUrl, header, fields, file, file1)
                call.enqueue(object : Callback<JsonElement> {
                    override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                        GrigoraApp.getInstance()?.updateData(key, t.message)
                        hideLoader()
                        showSnack(false, context, viewGroup, context.getString(R.string.internetnotavailable))

                    }

                    override fun onResponse(
                        call: Call<JsonElement>,
                        response: Response<JsonElement>
                    ) {
                        if (response.isSuccessful) {
                            hideLoader()
                            GrigoraApp.getInstance()?.updateData(key, response.body())
                        } else {
//                            response.errorBody().toString()
                            hideLoader()
                            showSnack(false, context, viewGroup, CommonUtils.parseError(response)!!)
//                            MyApplication.instance?.updateData(key, response.errorBody()!!.string())
//                            Toast.makeText(context, "Something went wrong!!!", Toast.LENGTH_SHORT).show()

                        }
                    }
                })


            } else {
                hideLoader()
                showSnack(
                    false,
                    context,
                    viewGroup,
                    context.getString(R.string.internetnotavailable)
                )
            }
        }









        fun postFormData(
            url: String,
            header: HashMap<String, Any?>,
            fields: HashMap<String, Any?>,
            loader_msg: String,
            context: Context, viewGroup: ViewGroup, key: Int
        ) {
            try {

                showLoader(context, loader_msg)
                var fullUrl = ApiConstants.BASE_URL + url;
                if (checkConnection(context)) {

                    var call: Call<JsonElement> =
                        ApiClient.getApiServices().postData(fullUrl, header, fields)
                    call.enqueue(object : Callback<JsonElement> {
                        override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                            GrigoraApp.getInstance()?.updateData(key, t.message)
                            hideLoader()
                            showSnack(false, context, viewGroup, context.getString(R.string.internetnotavailable))

                        }

                        override fun onResponse(
                            call: Call<JsonElement>,
                            response: Response<JsonElement>
                        ) {
                            if (response.isSuccessful) {
                                GrigoraApp.getInstance()?.updateData(key, response.body())
                                hideLoader()
                            } else {
//                            GrigoraApp.getInstance()?.updateData(key, response.errorBody()!!.string())

                                hideLoader()
                                showSnack(
                                    false,
                                    context,
                                    viewGroup,
                                    CommonUtils.parseError(response)!!
                                )

                            }
                        }
                    })


                } else {
                    hideLoader()

                    showSnack(
                        false,
                        context,
                        viewGroup,
                        context.getString(R.string.internetnotavailable)
                    )
                }
            } catch (e: Exception) {

                Log.d("exc", e.message)
            }


        }


//        fun showDialogSuccess(context: Activity, title: String, msg: String, finish: Boolean) {
//            val dialog = Dialog(context)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(false)
//            dialog.setContentView(R.layout.dialog_success)
//            val body = dialog.findViewById(R.id.tv_msg) as TextView
//            val dialog_title = dialog.findViewById(R.id.dialog_title) as TextView
//            body.text = msg
//            dialog_title.text = title
//            val btn_ok = dialog.findViewById(R.id.btn_ok) as Button
//            btn_ok.setOnClickListener {
//                dialog.dismiss()
//                if (finish) {
//                    context.finish()
//                }
//
//            }
//
//            dialog.show()
//
//        }
/////////////////////////////////////////////////////////////
//        fun getLocationFromAddress(strAddress: String, context: Context): GeoPoint {
//
//            var coder: Geocoder();
//            List<Address> address;
//            GeoPoint p1 = null;
//
//            try {
//                address = coder.getFromLocationName(strAddress, 5);
//                if (address == null) {
//                    return null;
//                }
//                Address location = address . get (0);
//                location.getLatitude();
//                location.getLongitude();
//
//                p1 = new GeoPoint ((double)(location.getLatitude() * 1E6),
//                (double)(location.getLongitude() * 1E6));
//
//                return p1;
//            }
//
//


//        private val TAG = "GeocodingLocation"
//
//        fun getAddressFromLocation(
//            locationAddress: String,
//            context: Context, handler: Handler
//        ): String {
//            val thread = object : Thread() {
//                override fun run() {
//                    val geocoder = Geocoder(context, Locale.getDefault())
//                    var result: String? = null
//                    try {
//                        val addressList = geocoder.getFromLocationName(locationAddress, 1)
//                        if (addressList != null && addressList.size > 0) {
//                            val address = addressList[0]
//                            val sb = StringBuilder()
//                            sb.append(address.latitude).append("\n")
//                            sb.append(address.longitude).append("\n")
//                            result = sb.toString()
//                        }
//                    } catch (e: IOException) {
//                        Log.e(TAG, "Unable to connect to Geocoder", e)
//                    } finally {
//                        val message = Message.obtain()
//                        message.target = handler
//                        if (result != null) {
//                            message.what = 1
//                            val bundle = Bundle()
//                            result = "Address: " + locationAddress +
//                                    "\n\nLatitude and Longitude :\n" + result
//                            bundle.putString("address", result)
//                            message.data = bundle
//                        } else {
//                            message.what = 1
//                            val bundle = Bundle()
//                            result = "Address: " + locationAddress +
//                                    "\n Unable to get Latitude and Longitude for this address location."
//                            bundle.putString("address", result)
//                            message.data = bundle
//                        }
//                        message.sendToTarget()
//                    }
//                }
//            }
//            thread.start()
//        }

    }

}


