package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class ChooseLanguageViewModel : ViewModel() {
    var langSelected: MutableLiveData<String> = MutableLiveData()

    init {
        langSelected.value = "1"
    }

    var isLoading = MutableLiveData<Boolean>()
    var langSelectionResult = MutableLiveData<Any>()

    fun setSelectedLanguage() {
        isLoading.value = true
        langSelectionResult.value = langSelected.value
        isLoading.value = false

        ApiRepo.getInstance()
            .setLanguage(
                langSelected.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    langSelectionResult.value =
                        Gson().fromJson(
                            result as JsonElement,
                            CommonResponseModel::class.java
                        )
                } else {
                    langSelectionResult.value = result
                }
            }
    }
}