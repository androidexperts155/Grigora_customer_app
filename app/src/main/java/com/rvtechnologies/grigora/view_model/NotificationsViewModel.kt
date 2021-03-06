package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.NotificationsModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils

class NotificationsViewModel : ViewModel() {

    var notificationsRes = MutableLiveData<Any>()
    var notificationsDelete = MutableLiveData<Any>()
    var isLoading = MutableLiveData<Boolean>()


    fun getNotifications(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getNotifications(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<NotificationsModel>>>() {}.type
                    notificationsRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    notificationsRes.value = result
                }
            }
    }

    fun deleteNotification(token: String, id: String, showLoader: Boolean) {
        if (showLoader)
            isLoading.value = true

        ApiRepo.getInstance()
            .deleteNotification(
                token = token, notificationId = id
            ) { success, result ->
                isLoading.value = false
//                if (success) {
//                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
//                    notificationsDelete.value = Gson().fromJson(result as JsonElement, type)
//                } else {
//                    notificationsDelete.value = result
//                }
            }
    }

    fun deleteAllNotification() {
        isLoading.value = true

        ApiRepo.getInstance()
            .deleteNotification(
                token = CommonUtils.getToken(), notificationId = ""
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
                    notificationsDelete.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    notificationsDelete.value = result
                }
            }
    }


}
