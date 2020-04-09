package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class RestaurantDetailGroupViewModel : ViewModel() {
    var id: MutableLiveData<String> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var cartId: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var restaurantDetail: MutableLiveData<Any> = MutableLiveData()
    var addCartRes: MutableLiveData<Any> = MutableLiveData()
    var cartItemList: MutableLiveData<Any> = MutableLiveData()

    fun getRestaurantsDetailsCart(
        token: String,
        restId: String,
        price: String,
        cartId: String,
        filter: String
    ) {
        ApiRepo.getInstance()
            .getRestaurantsDetailsCart(
                token,
                restId, price, cartId, filter
            ) { success, result ->
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<RestaurantDetailNewModel>>() {}.type
                    restaurantDetail.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantDetail.value = result
                }
            }
    }

    fun getCartItems(cartId: String, itemId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getGroupItemCart(
                itemId,
                cartId
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
//            ApiRepo.getInstance()
//                .addItemToGroupCart(
//                    token = token.value.toString(),
//                    restaurantId = restaurantId,
//                    itemId = itemId,
//                    price = price,
//                    quantity = quantity,
//                    itemChoices = "", cartId = cartId.value.toString()
//                ) { success, result ->
//                    isLoading.value = false
//                    if (success) {
//                        val type = object : TypeToken<CommonResponseModel<AddCartModel>>() {}.type
//                        addCartRes.value = Gson().fromJson(result as JsonElement, type)
//                    } else
//                        addCartRes.value = result
//                }
        }
    }

    fun updateType(restaurantId: String, type: String, token: String) {
        ApiRepo.getInstance()
            .changeOrderType(
                token = token,
                restaurant_id = restaurantId,
                cart_type = type
            ) { success, result ->
                isLoading.value = false
//                if (success) {
//                    val type = object : TypeToken<CommonResponseModel<AddCartModel>>() {}.type
//                    addCartRes.value = Gson().fromJson(result as JsonElement, type)
//                }
//                else
//                    addCartRes.value = result
            }

    }
}
