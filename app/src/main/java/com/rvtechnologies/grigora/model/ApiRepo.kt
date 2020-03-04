package com.rvtechnologies.grigora.model

import android.util.Log
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.api.ApiClient
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepo {
    companion object {
        private var ApiRepoInstance: ApiRepo? = null
        fun getInstance() = ApiRepoInstance
            ?: ApiRepo().also {
                ApiRepoInstance = it
            }
    }

    /*
   Set Language data
    */

    fun setLanguage(
        language: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().setLang(language)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    /*
    Login repo
     */

    fun login(
        email: String,
        password: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().login(email, password, AppConstants.ROLE_CUSTOMER)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun phoneLogin(
        phone: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().phoneLogin(phone, "2")
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun socialLogin(
        name: String,
        email: String,
        phone: String,
        loginType: String,
        socialId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().socialLogin(name, email, phone, loginType, socialId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    /*
  Signup repo
   */

    fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String,
        password_confirmation: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient()
            .signUp(name, email, phone, password, password_confirmation, AppConstants.ROLE_CUSTOMER)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    /*
 Forgot Password repo
  */

    fun forgotPassword(
        email: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().forgotPassword(email)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    /*
Cuisine repo
*/


    fun getCuisine(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getCuisine(token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    // get all addresses repo
    fun getAllAddresses(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getAllAddresses(token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    // get all addresses repo
    fun addAddress(
        map: HashMap<String, String>,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().addAddress(
            map[AppConstants.TOKEN]!!,
            map[AppConstants.ADDRESS]!!,
            map[AppConstants.LATITUDE]!!,
            map[AppConstants.LONGITUDE]!!,
            map[AppConstants.LOCATION_TYPE_ID]!!,
            map[AppConstants.COMPLETE_ADDRESS]!!
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun getCurrentOrders(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getCurrentOrders(token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun getPastOrders(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getPastOrders(token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun getUpcomingOrders(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getUpcomingOrders(token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun rateDriver(
        token: String,
        receiverId: String,
        rating: String,
        review: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().rateDriver(
            token = token,
            receiverId = receiverId,
            orderId = orderId,
            rating = rating,
            receiver_type = "3",
            review = review
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun rateRestaurant(
        token: String,
        receiverId: String,
        rating: String,
        review: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().rateRestaurant(
            token = token,
            receiverId = receiverId,
            orderId = orderId,
            rating = rating,
            receiver_type = "2",
            review = review
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun rateMeals(
        token: String,
        rating: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().rateMeals(
            token = token,
            rating = rating,
            orderId = orderId
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun likeOrUnlike(
        token: String,
        restaurantId: String,
        status: String,

        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().likeOrUnlike(
            token = token,
            restaurant_id = restaurantId,
            status = status
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful) {
                        onResult(true, response.body()!!)
                        Log.e("fav", response.body().toString())
                    } else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun getAllCategories(
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {

        ApiClient.getClient().getAllCategories()
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun getRestaurants(
        lat: String,
        lng: String,
        sort: String,
        search: String,
        user_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getRestaurants(lat, lng, sort, search, user_id)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful) {
                        onResult(true, response.body()!!)
                        Log.e("rest", response.body().toString())
                    } else {
                        onResult(false, CommonUtils.parseError(response))
                        Log.e("rest unsucces", CommonUtils.parseError(response)!!)

                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                    Log.e("rest unsucces", t?.message!!)

                }

            })
    }

    fun getPopularRestaurants(
        token: String,
        lat: String,
        lng: String,

        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getPopularRestaurants(token = token, lat = lat, lng = lng)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun searchRestaurants(
        token: String,
        latitude: String,
        longitude: String,
        search: String,
        filter_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().searchRestaurants(
            token = token,
            latitude = latitude,
            longitude = longitude,
            search = search,
            filter_id = filter_id
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun getRestaurantsByCuisine(
        token: String,
        filter_type: String,
        latitude: String,
        longitude: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getRestaurantsByCuisine(token, filter_type, latitude, longitude)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getMenuItemByCategory(
        id: String, lat: String, lang: String,
        user_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getMenuItemByCategory(id = id, lat = lat, long = lang, user_id = user_id)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful) {


                        onResult(true, response.body()!!)
                    } else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getRestaurantsDetails(
        token: String,
        restId: String,
        price: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getRestaurantDetails(token, restId, price)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getRestaurantsDetailsCart(
        token: String,
        restId: String,
        price: String,
        cartId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getRestaurantDetailsCart(token, restId, price, cartId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getItemCart(
        token: String,
        itemId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getItemCart(token, itemId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun clearCart(
        token: String,
        cartId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().removeCart(token,cartId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getOffers(
        token: String, id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getOffers(token, id)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun addItemToCart(
        token: String,
        restaurantId: String,
        itemId: String,
        quantity: String,
        price: String,
        itemChoices: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().addItemToCart(
            token = token,
            restaurantId = restaurantId,
            itemId = itemId,
            price = price,
            quantity = quantity,
            item_choices = itemChoices
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun addItemToGroupCart(

        token: String,
        cartId: String,
        restaurantId: String,
        itemId: String,
        quantity: String,
        price: String,
        itemChoices: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().addItemToGroupCart(
            token = token,
            cartId = cartId,
            restaurantId = restaurantId,
            itemId = itemId,
            price = price,
            quantity = quantity,
            item_choices = itemChoices
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun changeOrderType(
        token: String,
        cart_type: String,
        cart_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().changeOrderType(
            token = token,
            cart_id = cart_id,
            cart_type = cart_type
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }


    fun updateCartQty(
        token: String,
        cartItemId: String,
        quantity: String,
        cart_id: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().updateCartQty(
            token = token,
            cartItemId = cartItemId,
            cartId = cart_id,
            quantity = quantity
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun viewCart(
        token: String,
        latitude: String,
        logitude: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().viewCart(token = token, latitude = latitude, logitude = logitude)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun viewGroupCart(
        token: String,
        latitude: String,
        logitude: String,
        cartId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().viewGroupCart(
            token = token,
            latitude = latitude,
            logitude = logitude,
            cart_id = cartId
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun postData(
        url: String,
        headers: HashMap<String, Any>,
        body: HashMap<String, Any?>,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().postData(url, headers, body)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun placeOrder(
        token: String,
        cart_id: String,
        promo_id: String,
        app_fee: String,
        delivery_fee: String,
        price_before_promo: String,
        price_after_promo: String,
        final_price: String,
        payment_method: String,
        reference: String,
        delivery_address: String,
        delivery_lat: String,
        delivery_long: String,
        delivery_note: String,
        carttype: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().placeOrder(
            token = token,
            cart_id = cart_id,
            promo_id = promo_id,
            app_fee = app_fee,
            delivery_fee = delivery_fee,
            price_before_promo = price_before_promo,
            price_after_promo = price_after_promo,
            final_price = final_price,
            payment_method = payment_method,
            reference = reference,
            delivery_address = delivery_address,
            delivery_lat = delivery_lat,
            delivery_long = delivery_long,
            delivery_note = delivery_note, order_type = carttype
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun scheduleOrder(
        token: String,
        cart_id: String,
        promo_id: String,
        app_fee: String,
        delivery_fee: String,
        price_before_promo: String,
        price_after_promo: String,
        final_price: String,
        payment_method: String,
        schedule_time: String,
        delivery_address: String,
        delivery_lat: String,
        delivery_long: String,
        delivery_note: String,
        carttype: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().scheduleOrder(
            token = token,
            cart_id = cart_id,
            promo_id = promo_id,
            app_fee = app_fee,
            delivery_fee = delivery_fee,
            price_before_promo = price_before_promo,
            price_after_promo = price_after_promo,
            final_price = final_price,
            payment_method = payment_method,
            schedule_time = schedule_time,
            delivery_address = delivery_address,
            delivery_lat = delivery_lat,
            delivery_long = delivery_long,
            delivery_note = delivery_note, order_type = carttype
        )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun payStack(
        token: String,
        amount: String,
        email: String,
        reference: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getPayStack(token = token, amount = amount, email = email, reference = reference)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getLocationTypes(
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getLocationTypes()
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getOrderDetails(
        token: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getOrderDetails(token = token, orderId = orderId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }
            })
    }

    fun getProfileData(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getProfileData(token = token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun saveProfile(
        token: String,
        name: RequestBody,
        phone: RequestBody,
        image: MultipartBody.Part,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().saveProfile(token, name, phone, image)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun changePassword(
        token: String,
        oldPassword: String,
        password: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().changePassword(token, oldPassword, password)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getRestaurantReviews(
        restaurantId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().getRestaurantReviews(restaurantId)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun cancelOrder(
        token: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().cancelOrder(orderId, token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun completePickupOrder(
        token: String,
        orderId: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().completePickupOrder(orderId, token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun searchUser(
        token: String,
        email: String,

        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient().searchUser(token = token, email = email)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun transferMoney(
        token: String,
        walletId: String,
        amount: String,

        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .transferMoney(token = token, walletId = walletId, amount = amount)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun walletHistory(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .walletHistory(token = token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun addMoney(
        token: String,
        transactionData: String,
        amount: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .addMoney(token = token, amount = amount, transactionData = transactionData)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getDashboardData(
        map: HashMap<String, Any>,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getDashboardData(body = map, token = map["token"].toString())
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getQuizQuestion(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getQuizQuestion(token = token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun logout(
        token: String,
        deviceToken: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .logout(token = token, device_id = deviceToken)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun submitAnswer(
        token: String,
        question_id: String,
        answer: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .submitAnswer(token = token, question_id = question_id, answer = answer)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getPickupRestaurants(
        token: String,
        latitude: String,
        logitude: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getPickupRestaurants(token = token, latitude = latitude, logitude = logitude)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun getNotifications(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getNotifications(token = token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getFaq(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getFaq(token = token)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun changeDeliveryToPickup(
        token: String,
        order_id: String,
        status: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .changeDeliveryToPickup(token = token, order_id = order_id, status = status)
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun createGroupOrder(
        token: String,
        restaurant_id: String,
        max_per_person: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .createGroupOrder(
                token = token,
                restaurant_id = restaurant_id,
                max_per_person = max_per_person

            )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

    fun getGroupOrders(
        token: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .getAllGroupOrders(
                token = token

            )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun saveCartLink(
        token: String,
        cart_id: String,
        share_link: String,

        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .saveCartLink(
                token = token,
                cart_id = cart_id,
                share_link = share_link
            )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }


    fun showFilterData(
        token: String,
        filter_type: String,
        latitude: String,
        longitude: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        ApiClient.getClient()
            .showFilterData(
                token = token,
                filter_type = filter_type,
                latitude = latitude,
                longitude = longitude
            )
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>?,
                    response: Response<JsonElement>?
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)
                    else {
                        onResult(false, CommonUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    onResult(false, t?.message)
                }

            })
    }

}