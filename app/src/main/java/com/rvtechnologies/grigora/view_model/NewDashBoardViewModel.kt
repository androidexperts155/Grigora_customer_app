package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.model.models.OrderItemModel

class NewDashBoardViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var dashboardResult = MutableLiveData<Any>()


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
}
