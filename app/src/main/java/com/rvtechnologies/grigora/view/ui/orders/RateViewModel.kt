package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.model.models.RateOrderModel
import com.rvtechnologies.grigora.utils.AppConstants

class RateViewModel : ViewModel() {
    var orderItemModel: MutableLiveData<OrderItemModel> = MutableLiveData()
    var rateText: MutableLiveData<String> = MutableLiveData()
    var image: MutableLiveData<String> = MutableLiveData()
    var valid: MutableLiveData<Boolean> = MutableLiveData()
    var isDriver: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var rating: MutableLiveData<Float> = MutableLiveData()
    var review: MutableLiveData<String> = MutableLiveData()


    var rateResult: MutableLiveData<Any> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun rateDriver(
    ) {
//
//        if (rating.value == null || rating.value?.compareTo(0.0) == 0) {
//            valid.value = false
//        } else {
//            isLoading.value = true
//            ApiRepo.getInstance()
//                .addRatingReview(
//                    token = token.value!!,
//                    receiverId = if (isDriver.value!!) orderItemModel.value?.driverId.toString() else orderItemModel.value?.restaurantId.toString(),
//                    orderId = orderItemModel.value?.id.toString(),
//                    rating = rating.value.toString(),
//                    receiver_type = if (isDriver.value!!) AppConstants.REVIEW_DRIVER else AppConstants.REVIEW_RESTAURANT,
//                    review = review.value.toString()
//                ) { success, result ->
//                    isLoading.value = false
//                    if (success) {
//                        val type =
//                            object : TypeToken<CommonResponseModel<RateOrderModel>>() {}.type
//                        rateResult.value = Gson().fromJson(result as JsonElement, type)
//
//                    } else {
//                        rateResult.value = result
//                    }
//                }
//        }
    }
}
