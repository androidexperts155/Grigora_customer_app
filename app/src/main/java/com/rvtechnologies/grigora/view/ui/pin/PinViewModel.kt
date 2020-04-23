package com.rvtechnologies.grigora.view.ui.pin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class PinViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var addPinRes = MutableLiveData<Any>()
    var changePinRes = MutableLiveData<Any>()


    fun addPin(pin: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .addPin(
                pin

            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<*>>() {}.type
                    addPinRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    addPinRes.value = result
                }
            }
    }

    fun changePin(pin: String, newPin: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .changePin(
                pin, newPin
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<*>>() {}.type
                    changePinRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    changePinRes.value = result
                }
            }
    }
}
