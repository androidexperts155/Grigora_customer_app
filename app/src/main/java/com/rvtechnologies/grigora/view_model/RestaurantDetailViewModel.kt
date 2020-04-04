package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
 import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class RestaurantDetailViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var restaurantDetail: MutableLiveData<Any> = MutableLiveData()

    fun getRestaurantsDetails(restId: String, price: String) {
//        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantsDetails(
                CommonUtils.getToken(),
                restId, price
            ) { success, result ->
//                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<RestaurantDetailNewModel>>() {}.type
                    restaurantDetail.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantDetail.value = result
                }
            }
    }



    fun getRestaurantsDetailsCart(token: String, restId: String, price: String, cartId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantsDetailsCart(
                token,
                restId, price, cartId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<RestaurantDetailNewModel>>() {}.type
                    restaurantDetail.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantDetail.value = result
                }
            }
    }

    fun updateType(restaurantId: String, type: String, token: String) {
        ApiRepo.getInstance()
            .changeOrderType(
                token = token,
                restaurant_id = restaurantId,
                cart_type = type
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
//                    addCartRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
//                    addCartRes.value = result
                }
            }

    }
}
