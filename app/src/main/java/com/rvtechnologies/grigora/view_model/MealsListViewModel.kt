package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.MealsListModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class MealsListViewModel : ViewModel() {



    var isLoading = MutableLiveData<Boolean>()
    var mealsist: MutableLiveData<Any> = MutableLiveData()

    fun getMeals(cuisineId:String,cartId:String,filter:String) {
        isLoading.value=true
        ApiRepo.getInstance()
            .getMeals(
                cuisineId,cartId,filter
            ) { success, result ->
                isLoading.value=false

                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<MealsListModel>>() {}.type
                     mealsist.value = Gson().fromJson(result as JsonElement, type)

                } else {
                     mealsist.value = result
                }
            }
    }
}
