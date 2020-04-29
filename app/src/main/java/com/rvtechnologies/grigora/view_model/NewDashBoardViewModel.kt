package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.AllCartModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel

class NewDashBoardViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var dashboardResult = MutableLiveData<Any>()
    var allCartResult = MutableLiveData<Any>()
    var mergeCartResult = MutableLiveData<Any>()
    var checkUnderLocationResult = MutableLiveData<Any>()

    fun getDashboardData(map: HashMap<String, Any>) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getDashboardData(map) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<NewDashboardModel>>() {}.type
                    dashboardResult.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    dashboardResult.value = result
                }
            }
    }

    fun getAllCart() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getAllCarts() { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<AllCartModel>>() {}.type
                    allCartResult.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    allCartResult.value = result
                }
            }
    }

    fun mergeCart(cartId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .mergeCarts(cartId) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
                    mergeCartResult.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    mergeCartResult.value = result
                }
            }
    }


    fun checkUnderLocation() {
        isLoading.value = true
        ApiRepo.getInstance()
            .checkUnderLocation { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
                    checkUnderLocationResult.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    checkUnderLocationResult.value = result
                }
            }
    }
}



