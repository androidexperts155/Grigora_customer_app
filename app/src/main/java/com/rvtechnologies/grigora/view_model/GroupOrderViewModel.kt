package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.CreateGroupOrderModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class GroupOrderViewModel : ViewModel() {
    var createGroupOrderRes = MutableLiveData<Any>()
    var updateCartLink = MutableLiveData<Any>()
    var isLoading = MutableLiveData<Boolean>()

    fun createGroupOrder(token: String, max: String, resId: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .createGroupOrder(
                token = token,
                max_per_person = max, restaurant_id = resId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<CreateGroupOrderModel>>() {}.type
                    createGroupOrderRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    createGroupOrderRes.value = result
                }
            }
    }


    fun saveCartLink(token: String, cartId: String, link: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .saveCartLink(
                token = token,
                share_link = link,
                cart_id = cartId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<CreateGroupOrderModel>>() {}.type
                    updateCartLink.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    updateCartLink.value = result
                }
            }
    }


}
