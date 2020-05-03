package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.CommonUtils

class ScheduleOrderDetailsViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var cancelOrderRes = MutableLiveData<Any>()

    fun cancelOrder(id:String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .cancelOrder(
                token = CommonUtils.getToken(),
                orderId = id
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

}
