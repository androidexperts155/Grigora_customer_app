package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class ContactUsViewModel : ViewModel() {
    var faqRes = MutableLiveData<Any>()
    var subIssuesRes = MutableLiveData<Any>()
    var isLoading = MutableLiveData<Boolean>()

    fun getFaq(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getFaq(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<FaqModel>>>() {}.type
                    faqRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    faqRes.value = result
                }
            }
    }

    fun getSubIssues(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getSubIssues(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<SubIssueModel>>>() {}.type
                    subIssuesRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    subIssuesRes.value = result
                }
            }
    }
}
