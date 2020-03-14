package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.PickupRestaurantsModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel

class PickupRestaurantsViewModel : ViewModel() {
    var restaurantsResponse: MutableLiveData<Any> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun getRestaurants( lat: String, lng: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getPickupRestaurants(
                 lat, lng
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<PickupRestaurantsModel>>() {}.type

                    restaurantsResponse.value = Gson().fromJson((result as JsonElement), type)
                } else {
                    restaurantsResponse.value = result
                }
            }
    }
}
