package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.DashboardSearchModel
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.api.ApiClient
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRestaurantViewModel : ViewModel() {
    lateinit var callback: Call<JsonElement>
    var lat: MutableLiveData<Double> = MutableLiveData()
    var lng: MutableLiveData<Double> = MutableLiveData()
    var sort: MutableLiveData<String> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var restaurantListRes: MutableLiveData<Any> = MutableLiveData()


    fun getRestaurants(token: String, search: String, filter: String) {
//        isLoading.value = true
        searchRestaurants(
            token,
            lat.value.toString().trim(),
            lng.value.toString().trim(), search, filter
        ) { success, result ->
//            isLoading.value = false
            if (success) {
                val type =
                    object : TypeToken<CommonResponseModel<DashboardSearchModel>>() {}.type
                restaurantListRes.value = Gson().fromJson(result as JsonElement, type)

            } else {
                restaurantListRes.value = result
            }
        }
    }


    private fun searchRestaurants(
        token: String,
        latitude: String,
        longitude: String,
        search: String,
        filter_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {



        if(::callback.isInitialized){
            callback.cancel()
        }

        callback = ApiClient.getClient().searchRestaurants(
            token = token,
            latitude = latitude,
            longitude = longitude,
            search = search,
            filter_id = filter_id
        )

        callback.enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>?,
                response: Response<JsonElement>?
            ) {
                if (response != null && response.isSuccessful)
                    onResult(true, response.body()!!)
                else {
                    onResult(false, CommonUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                onResult(false, t?.message)
            }
        })
    }

}
