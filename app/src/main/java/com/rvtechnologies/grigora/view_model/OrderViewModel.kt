package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel

class OrderViewModel : ViewModel() {

    var orderListRes: MutableLiveData<Any> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun getCurrentOrder(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getCurrentOrders(
                token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<OrderItemModel>>>() {}.type
                    orderListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    orderListRes.value = result
                }
            }
    }

    fun getPastOrder(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getPastOrders(
                token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<OrderItemModel>>>() {}.type
                    orderListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    orderListRes.value = result
                }
            }
    }

    fun getUpcomingOrders(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getUpcomingOrders(
                token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<OrderItemModel>>>() {}.type
                    orderListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    orderListRes.value = result
                }
            }
    }

    fun rateDriver(token: String, orderId: String, driverId: String, rating: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .rateDriver(
                token = token,
                orderId = orderId, receiverId = driverId, rating = rating, review = ""
            ) { success, result ->
                isLoading.value = false
            }
    }

    fun rateRestaurant(token: String, orderId: String, restaurantId: String, rating: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .rateRestaurant(
                token = token,
                orderId = orderId, receiverId = restaurantId, rating = rating, review = ""
            ) { success, result ->
                isLoading.value = false
            }
    }

    fun rateMeals(token: String, orderId: String, ratings: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .rateMeals(
                token = token,
                orderId = orderId, rating = ratings
            ) { success, result ->
                isLoading.value = false
            }
    }

}
