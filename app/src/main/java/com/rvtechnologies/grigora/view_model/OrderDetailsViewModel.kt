package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.model.models.RateOrderModel

class OrderDetailsViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var token = MutableLiveData<String>()
    var orderId = MutableLiveData<String>()
    var cancelOrderRes = MutableLiveData<Any>()
    var completePickupOrderRes = MutableLiveData<Any>()
    var ratingResult = MutableLiveData<Any>()
    var changeDeliveryToPickup = MutableLiveData<Any>()
    var orderItemModel = MutableLiveData<OrderItemModel>()

    var orderItemRes = MutableLiveData<Any>()
    fun getOrderDetails() {
        isLoading.value = true

        ApiRepo.getInstance()
            .getOrderDetails(
                token = token.value.toString(),
                orderId = orderId.value!!
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<OrderItemModel>>() {}.type
                    orderItemRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    orderItemRes.value = result
                }
            }
    }

    fun cancelOrder() {
        isLoading.value = true
        ApiRepo.getInstance()
            .cancelOrder(
                token = token.value.toString(),
                orderId = orderId.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<OrderItemModel>>() {}.type
                    cancelOrderRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    cancelOrderRes.value = result
                }
            }
    }

    fun rateDriver(driverId: String, rating: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .rateDriver(
                token = token.value.toString(),
                orderId = orderId.value!!, receiverId = driverId, rating = rating, review = ""
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<RateOrderModel>>() {}.type
                    ratingResult.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    ratingResult.value = result
                }
            }
    }

    fun completeOrder() {
        isLoading.value = true
        ApiRepo.getInstance()
            .completePickupOrder(
                token = token.value.toString(),
                orderId = orderId.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<OrderItemModel>>() {}.type
                    completePickupOrderRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    completePickupOrderRes.value = result
                }
            }
    }


    fun changeDeliveryToPickup(status: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .changeDeliveryToPickup(
                token = token.value.toString(),
                order_id = orderId.value.toString().trim(), status = status
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    changeDeliveryToPickup.value =
                        Gson().fromJson(result as JsonElement, CommonResponseModel::class.java)
                } else {
                    changeDeliveryToPickup.value = result
                }
            }
    }
}
