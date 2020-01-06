package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
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

    fun getCuisines(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getCuisine(
                token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<ArrayList<CuisineModel>>>() {}.type
                    cuisineListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    cuisineListRes.value = result
                }
            }
    }


    fun getRestaurants(searchText:String, id:String) {

        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurants(
                lat.value.toString().trim(),
                lng.value.toString().trim(),
                sort.value.toString().trim(),
                searchText,
                id

            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<ArrayList<RestaurantModel>>>() {}.type
                    restaurantListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantListRes.value = result
                }
            }
    }


    fun getRestaurantsByCuisine(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantsByCuisine(
                token,
                id.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<ArrayList<RestaurantModel>>>() {}.type
                    restaurantListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantListRes.value = result
                }
            }
    }



}