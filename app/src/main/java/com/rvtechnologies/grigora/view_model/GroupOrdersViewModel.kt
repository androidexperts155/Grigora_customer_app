package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.GroupOrdersModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class GroupOrdersViewModel : ViewModel() {

    var groupOrdersRes = MutableLiveData<Any>()
    var isLoading = MutableLiveData<Boolean>()


    fun getGroupOrders(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getGroupOrders(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object :
                            TypeToken<CommonResponseModel<Collection<GroupOrdersModel>>>() {}.type
                    groupOrdersRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    groupOrdersRes.value = result
                }
            }
    }
}
