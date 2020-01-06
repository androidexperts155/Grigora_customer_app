package com.rvtechnologies.grigora.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants

class DashboardViewModel : ViewModel() {
    var lat: MutableLiveData<Double> = MutableLiveData()
    var lng: MutableLiveData<Double> = MutableLiveData()

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var categoryListRes: MutableLiveData<Any> = MutableLiveData()
    var restaurantListRes: MutableLiveData<Any> = MutableLiveData()
    var menuListByCategoryRes: MutableLiveData<Any> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()
    var user_id: MutableLiveData<String> = MutableLiveData()

    fun getCuisines(token: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getCuisine(
                token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<CuisineModel>>>() {}.type
                    categoryListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    categoryListRes.value = result
                }
            }
    }

    fun getAllCategories() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getAllCategories { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<CategoryModel?>?>>() {}.type
                    categoryListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    categoryListRes.value = result
                }
            }
    }


    //    fun getPopularRestaurants(token: String, id: String) {
    fun getPopularRestaurants(id: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getPopularRestaurants(
                lat = lat.value.toString().trim(),
                lng = lng.value.toString().trim(),
                id = id
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<RestaurantModel>>>() {}.type
                    restaurantListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantListRes.value = result
                }
            }
    }


    fun getMenuItemByCategory() {
        if (id.value != null) {
            isLoading.value = true
            ApiRepo.getInstance()
                .getMenuItemByCategory(
                    id = id.value.toString().trim(),
                    lat = lat.value.toString().trim(),
                    lang = lng.value.toString().trim(),
                    user_id = user_id.value.toString().trim()
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        val type = object :
                            TypeToken<CommonResponseModel<ArrayList<MenuItemModel>>>() {}.type
                        menuListByCategoryRes.value =
                            Gson().fromJson(result as JsonElement, type)

                    } else {
                        menuListByCategoryRes.value = result
                    }
                }
        }
    }

    fun click(isChecked: Int,id:Int,token:String) {

//        ApiRepo.getInstance()
//            .likeOrUnlike(
//                token = token,
//                restaurantId = restaurantId.value.toString(),
//                status = isChecked.toString()
//
//            ) { success, result ->
//                if (success) {
////                    val type =
////                        object : TypeToken<CommonResponseModel<RateOrderModel>>() {}.type
////                    rateResult.value = Gson().fromJson(result as JsonElement, type)
//
//                } else {
////                    rateResult.value = result
//                }
//            }
    }

}
