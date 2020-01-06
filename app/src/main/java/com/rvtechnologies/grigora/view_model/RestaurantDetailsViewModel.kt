package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.ItemChoicesModel
import com.rvtechnologies.grigora.model.models.MenuItemModel

class RestaurantDetailsViewModel : ViewModel() {

    var id: MutableLiveData<String> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var restaurantItemList: MutableLiveData<Any> = MutableLiveData()
    var cartItemList: MutableLiveData<Any> = MutableLiveData()


    fun getRestaurantsDetails(token: String, uid: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantsDetails(
                token,
                uid, id.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<MenuItemModel>>>() {}.type
                    restaurantItemList.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantItemList.value = result
                }
            }
    }

    fun getCartItems(token: String, itemId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getItemCart(
                token,
                itemId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<CartDetail>>>() {}.type
                    cartItemList.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    cartItemList.value = result
                }
            }
    }

    fun updateCartQty(token: String, itemId: String, cartId: String, quantity: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .updateCartQty(
                token,
                itemId, quantity, cartId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                }
            }
    }

    fun click(isChecked: Int) {
        ApiRepo.getInstance()
            .likeOrUnlike(
                token = token.value!!,
                restaurantId = id.value.toString(),
                status = isChecked.toString()
            ) { success, result ->
                if (success) {
//                    val type =
//                        object : TypeToken<CommonResponseModel<RateOrderModel>>() {}.type
//                    rateResult.value = Gson().fromJson(result as JsonElement, type)

                } else {
//                    rateResult.value = result
                }
            }
    }


    fun addItemToCart(restaurantId: String, itemId: String, price: String, quantity: String) {
        if (token.value.toString().isNotBlank()) {
            isLoading.value = true
            ApiRepo.getInstance()
                .addItemToCart(
                    token = token.value.toString(),
                    restaurantId = restaurantId,
                    itemId = itemId,
                    price = price,
                    quantity = quantity,
                    itemChoices = ""
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        Gson().fromJson(
                            result as JsonElement,
                            CommonResponseModel::class.java
                        )
                    }
                }
        }
    }


}
