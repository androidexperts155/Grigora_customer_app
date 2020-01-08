package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel

class AddressListViewModel : ViewModel() {

    var token: MutableLiveData<String> = MutableLiveData()
    /*
    Observers
     */
    var isLoading = MutableLiveData<Boolean>()
    var addressesResult = MutableLiveData<Any>()

        fun getAddresses() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getAllAddresses(
                token.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {

                    Log.e("Tag :: ", (result as JsonElement).toString())
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<AddressModel>>>() {}.type
                    addressesResult.value = Gson().fromJson(result, type)

                } else {
                    addressesResult.value = result
                }

            }
    }

}
