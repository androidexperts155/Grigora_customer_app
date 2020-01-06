package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel

class LocationTypeViewModel : ViewModel() {
    var locationTypeData: MutableLiveData<Any> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun getLocationTypeList() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getLocationTypes { success, result ->
                isLoading.value = false
                if (success) {
                    Log.e("Tag :: ", (result as JsonElement).toString())
                    val type = object : TypeToken<CommonResponseModel<ArrayList<LocationTypeModel>>>() {}.type
                    locationTypeData.value = Gson().fromJson(result, type)
                } else {
                    locationTypeData.value = result
                }
            }
    }
}
