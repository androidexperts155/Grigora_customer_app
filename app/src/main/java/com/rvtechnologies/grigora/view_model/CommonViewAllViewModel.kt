package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class CommonViewAllViewModel : ViewModel() {

    var restaurantsResponse: MutableLiveData<Any> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var lat: MutableLiveData<String> = MutableLiveData()
    var lng: MutableLiveData<String> = MutableLiveData()

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun getFilterData(filter: String) {
        isLoading.value = true


        ApiRepo.getInstance()
            .showFilterData(
                token = token.value!!,
                latitude = lat.value!!,
                longitude = lng.value!!,
                filter_type = filter
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object :
                            TypeToken<CommonResponseModel<PickupRestaurantsModel>>() {}.type

                    restaurantsResponse.value = Gson().fromJson((result as JsonElement), type)
                } else {
                    restaurantsResponse.value = result
                }
            }
    }

    fun getFilterData(filter: String, id: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getRestaurantsByCuisine(
                token = token.value!!,
                latitude = lat.value!!,
                longitude = lng.value!!,
                filter_type = id
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object :
                            TypeToken<CommonResponseModel<PickupRestaurantsModel>>() {}.type

                    restaurantsResponse.value = Gson().fromJson((result as JsonElement), type)
                } else {
                    restaurantsResponse.value = result
                }
            }
    }


}
