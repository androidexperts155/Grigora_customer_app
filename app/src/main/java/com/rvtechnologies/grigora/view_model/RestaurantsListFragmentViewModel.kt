package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.CuisineModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants


class RestaurantsListFragmentViewModel : ViewModel() {
    var lat: MutableLiveData<Double> = MutableLiveData()
    var lng: MutableLiveData<Double> = MutableLiveData()
    var sort: MutableLiveData<String> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var cuisineListRes: MutableLiveData<Any> = MutableLiveData()
    var restaurantListRes: MutableLiveData<Any> = MutableLiveData()


    fun getRestaurants(token: String) {

        isLoading.value = true
        ApiRepo.getInstance()
            .getPopularRestaurants(
                token,
                lat.value.toString().trim(),
                lng.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                    object : TypeToken<CommonResponseModel<PickupRestaurantsModel>>() {}.type

                    restaurantListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantListRes.value = result
                }
            }
    }

}