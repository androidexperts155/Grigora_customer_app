package com.rvtechnologies.grigora.view.ui.offers.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OfferModel

class OfferViewModel : ViewModel() {
    var token: MutableLiveData<String> = MutableLiveData()

    var offersListRes: MutableLiveData<Any> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

//    fun getOffers() {
//        isLoading.value = true
//        ApiRepo.getInstance()
//            .getOffers(
//                token.value.toString()
//            ) { success, result ->
//                isLoading.value = false
//                if (success) {
//                    val type = object : TypeToken<CommonResponseModel<ArrayList<OfferModel>>>() {}.type
//                    offersListRes.value = Gson().fromJson(result as JsonElement, type)
//
//                } else {
//                    offersListRes.value = result
//                }
//            }
//    }
}
