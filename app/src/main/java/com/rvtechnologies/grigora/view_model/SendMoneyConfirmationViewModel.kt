package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.TransferMoneyModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class SendMoneyConfirmationViewModel : ViewModel() {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()


}
