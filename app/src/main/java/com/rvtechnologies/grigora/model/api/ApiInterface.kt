package com.rvtechnologies.grigora.model.api

import com.google.gson.JsonElement
import com.rvtechnologies.grigora.utils.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {


    @GET(ApiConstants.CHANGE_LANGUAGE_URL + "/{langId}")
    fun setLang(
        @Path("langId") langId: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.LOGIN_URL)
    fun login(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.PHONE_LOGIN_URL)
    fun phoneLogin(
        @Field("phone") email: String,
        @Field("role") role: String
    ): Call<JsonElement>


    //login_type(1:instagram,2:twiter,3:facebook,4:google)
    @FormUrlEncoded
    @POST(ApiConstants.SOCIAL_LOGIN_URL)
    fun socialLogin(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("login_type") login_type: String,
        @Field("social_id") social_id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.SIGNUP_URL)
    fun signUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
        @Field("role") role: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.FORGOT_PASSWORD_URL)
    fun forgotPassword(
        @Field("email") email: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_CUISINE_URL)
    fun getCuisine(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_CURRENT_ORDERS_URL)
    fun getCurrentOrders(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_PAST_ORDERS_URL)
    fun getPastOrders(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @GET(ApiConstants.GET_ALL_ADDRESSES)
    fun getAllAddresses(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @GET(ApiConstants.GET_ALL_CATEGORIES_URL)
    fun getAllCategories(
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GET_RESTAURANTS_URL)
    fun getRestaurants(
        @Field("lat") name: String,
        @Field("long") email: String,
        @Field("sort") sort: String,
        @Field("search") search: String,
        @Field("user_id") user_id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.ADD_ADDRESS)
    fun addAddress(
        @Header("Authorization") token: String,
        @Field("address") name: String,
        @Field("latitude") email: String,
        @Field("longitude") sort: String,
        @Field("location_type_id") search: String,
        @Field("complete_address") user_id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.GET_POPULAR_RESTAURANT_URL)
    fun getPopularRestaurants(
//        @Header("Authorization") token: String,
        @Field("user_id") id: String,
        @Field("lat") lat: String,
        @Field("long") lng: String

    ): Call<JsonElement>


    @GET(ApiConstants.GET_RESTAURANTS_BY_CUISINE_URL + "/{id}")
    fun getRestaurantsByCuisine(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.GET_MENU_ITEM_BY_CATEGORY_URL)
    fun getMenuItemByCategory(
        @Field("id") id: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("user_id") user_id: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GET_RESTAURANTS_DETAILS)
    fun getRestaurantDetails(
        @Header("Authorization") token: String,
        @Field("restaurant_id") restaurant_id: String,
        @Field("price_range") price_range: String
    ): Call<JsonElement>

    @GET(ApiConstants.REMOVE_CART_URL)
    fun removeCart(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.CART_ITEMS_URL + "/{id}")
    fun getItemCart(
        @Header("Authorization") token: String,
        @Path("id") itemId: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.ADD_ITEM_URL)
    fun addItemToCart(
        @Header("Authorization") token: String,
        @Field("restaurant_id") restaurantId: String,
        @Field("item_id") itemId: String,
        @Field("quantity") quantity: String,
        @Field("price") price: String,
        @Field("item_choices") item_choices: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.UPDATE_CART_QTY)
    fun updateCartQty(
        @Header("Authorization") token: String,
        @Field("cart_item_id") cartItemId: String,
        @Field("quantity") quantity: String,
        @Field("cart_id") cartId: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_OFFER_LIST_URL + "/{id}")
    fun getOffers(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.VIEW_CART_URL)
    fun viewCart(
        @Header("Authorization") token: String,
        @Field("latitude") latitude: String,
        @Field("logitude") logitude: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.PLACE_ORDER_URL)
    fun placeOrder(
        @Header("Authorization") token: String,
        @Field("cart_id") cart_id: String,
        @Field("promo_id") promo_id: String,
        @Field("app_fee") app_fee: String,
        @Field("delivery_fee") delivery_fee: String,
        @Field("price_before_promo") price_before_promo: String,
        @Field("price_after_promo") price_after_promo: String,
        @Field("final_price") final_price: String,
        @Field("payment_method") payment_method: String,
        @Field("delivery_note") delivery_note: String,
        @Field("delivery_address") delivery_address: String,
        @Field("delivery_lat") delivery_lat: String,
        @Field("delivery_long") delivery_long: String,
        @Field("reference") reference: String,
        @Field("order_type") order_type: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.PAY_STACK)
    fun getPayStack(
        @Header("Authorization") token: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("reference") reference: String


    ): Call<JsonElement>

    @GET(ApiConstants.LOCATION_TYPES_URL)
    fun getLocationTypes(): Call<JsonElement>


    @GET(ApiConstants.GET_ORDER_DETAILS_URL + "/{id}")
    fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Call<JsonElement>


    @GET(ApiConstants.PROFILE_DETAILS_URL)
    fun getProfileData(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.ADD_RATING_REVIEW_URL)
    fun rateDriver(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiverId: String,
        @Field("rating") rating: String,
        @Field("review") review: String,
        @Field("receiver_type") receiver_type: String,
        @Field("order_id") orderId: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_RATING_REVIEW_URL)
    fun rateRestaurant(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiverId: String,
        @Field("rating") rating: String,
        @Field("review") review: String,
        @Field("receiver_type") receiver_type: String,
        @Field("order_id") orderId: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_RATING_REVIEW_URL_MEALS)
    fun rateMeals(
        @Header("Authorization") token: String,
        @Field("ratings") rating: String,
        @Field("order_id") orderId: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.LIKE_UNLIKE_URL)
    fun likeOrUnlike(
        @Header("Authorization") token: String,
        @Field("restaurant_id") restaurant_id: String,
        @Field("status") status: String

    ): Call<JsonElement>


    @Multipart
    @POST(ApiConstants.EDIT_PROFILE_URL)
    fun saveProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.CHANGE_PASSWORD_URL)
    fun changePassword(
        @Header("Authorization") token: String,
        @Field("old_password") receiverId: String,
        @Field("password") review: String
    ): Call<JsonElement>

    @GET(ApiConstants.RESTAURANT_REVIEW_URL + "/{id}")
    fun getRestaurantReviews(
        @Path("id") id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.UPDATE_FIREABSE_TOKEN)
    fun updateFirebaseToken(
        @Header("Authorization") token: String,
        @Field("device_token") deviceToken: String,
        @Field("device_type") deviceType: String,
        @Field("device_id") deviceId: String
    ): Call<JsonElement>

    @Multipart
    @POST
    fun postData(
        @Url url: String,
        @HeaderMap authenticator: Map<String, String>,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: ArrayList<MultipartBody.Part>,
        @Part file1: ArrayList<MultipartBody.Part>
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST
    fun postData(
        @Url url: String,
        @FieldMap body: HashMap<String, Any?>
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST
    fun postData(
        @Url url: String,
        @HeaderMap authenticator: HashMap<String, Any>,
        @FieldMap body: HashMap<String, Any?>
    ): Call<JsonElement>

    @GET(ApiConstants.CANCEL_ORDER + "/{id}")
    fun cancelOrder(

        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.COMPLETE_PICKUP_ORDER + "/{id}")
    fun completePickupOrder(

        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.SEARCH_USER)
    fun searchUser(
        @Header("Authorization") token: String,
        @Field("email") email: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.TRANSFER_MONEY)
    fun transferMoney(
        @Header("Authorization") token: String,
        @Field("email") email: String,
        @Field("amount") amount: String,
        @Field("reason") reason: String

    ): Call<JsonElement>

    @GET(ApiConstants.WALLET_HISTORY)
    fun walletHistory(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_MONEY)
    fun addMoney(
        @Header("Authorization") token: String,
        @Field("transaction_data") transactionData: String,
        @Field("amount") amount: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.DASHBOARD_DATA)
    fun getDashboardData(
        @Header("Authorization") token: String,
        @FieldMap body: HashMap<String, Any>
    ): Call<JsonElement>


    @GET(ApiConstants.QUIZ_QUESTION)
    fun getQuizQuestion(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.QUIZ_ANSWER)
    fun submitAnswer(
        @Header("Authorization") token: String,
        @Field("question_id") question_id: String,
        @Field("answer") answer: String
    ): Call<JsonElement>

    @GET(ApiConstants.NOTIFICATIONS_LIST)
    fun getNotifications(
        @Header("Authorization") token: String
    ): Call<JsonElement>
}