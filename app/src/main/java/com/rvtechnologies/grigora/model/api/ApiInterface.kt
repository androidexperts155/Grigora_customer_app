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


    @GET(ApiConstants.GET_UPCOMING_ORDERS_URL)
    fun getUpcomingOrders(
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
        @Header("Authorization") token: String,
        @Field("lat") lat: String,
        @Field("long") lng: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.PROMO_RESTAURANTS)
    fun promoRestaurants(
        @Header("Authorization") token: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("promo_id") promo_id: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.SEARCH_RESTAURANTS)
    fun searchRestaurants(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("search") search: String,
        @Field("filter_id") filter_id: String

    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.GET_RESTAURANTS_BY_CUISINE_URL)
    fun getRestaurantsByCuisine(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("id") id: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
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
        @Field("user_id") user_id: String,
        @Field("restaurant_id") restaurant_id: String,
        @Field("price_range") price_range: String,
        @Field("login_type") loginType: String,
        @Field("veg_filter") veg_filter: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GET_MEALS_LIST)
    fun getMeals(
        @Header("Authorization") token: String,
        @Field("cuisine_id") cuisine_id: String,
        @Field("login_type") login_type: String,
        @Field("user_id") user_id: String,
        @Field("cart_id") cart_id: String,
        @Field("veg_filter") veg_filter: String,
        @Field("restaurant_id") restaurant_id: String

    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.GET_RESTAURANTS_DETAILS)
    fun getRestaurantDetailsCart(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("restaurant_id") restaurant_id: String,
        @Field("price_range") price_range: String,
        @Field("cart_id") cart_id: String,
        @Field("login_type") login_type: String,
        @Field("veg_filter") veg_filter: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.REMOVE_CART_URL)
    fun removeCart(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
        @Field("cart_id") cart_id: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.CART_ITEMS_URL)
    fun getItemCart(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
        @Field("item_id") cart_id: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GROUP_CART_ITEMS_URL)
    fun getGroupItemCart(
        @Header("Authorization") token: String,
        @Field("cart_id") cart_id: String,
        @Field("item_id") item_id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.ADD_ITEM_URL)
    fun addItemToCart(
        @Header("Authorization") token: String,
        @Field("restaurant_id") restaurantId: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
        @Field("item_id") itemId: String,
        @Field("quantity") quantity: String,
        @Field("price") price: String,
        @Field("item_choices") item_choices: String,
        @Field("item_removeables") item_removeables: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_IN_GROUP_CART)
    fun addItemToGroupCart(
        @Header("Authorization") token: String,
        @Field("cart_id") cartId: String,
        @Field("restaurant_id") restaurantId: String,
        @Field("item_id") itemId: String,
        @Field("quantity") quantity: String,
        @Field("price") price: String,
        @Field("item_choices") item_choices: String,
        @Field("item_removeables") item_removeables: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.CHANGE_ORDER_TYPE)
    fun changeOrderType(
        @Header("Authorization") token: String,
        @Field("cart_type") cart_type: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
        @Field("restaurant_id") restaurant_id: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.UPDATE_CART_QTY)
    fun updateCartQty(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
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
        @Field("user_id") userId: String,
        @Field("login_type") loginType: String,
        @Field("latitude") latitude: String,
        @Field("logitude") logitude: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.VIEW_GROUP_CART_URL)
    fun viewGroupCart(
        @Header("Authorization") token: String,
        @Field("cart_id") cart_id: String,
        @Field("latitude") latitude: String,
        @Field("longitude") logitude: String
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
    @POST(ApiConstants.SCHEDULE_ORDER_URL)
    fun scheduleOrder(
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
        @Field("schedule_time") schedule_time: String,
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

    @GET(ApiConstants.GET_PURCHASED_CARDS)
    fun getPurchasedCards(
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
        @Field("order_id") orderId: String,
        @Field("bad_review") badReview: String,
        @Field("good_review") goodReview: String,
        @Field("amount") tipAmount: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.LOGOUT)
    fun logout(
        @Header("Authorization") token: String,
        @Field("device_id") device_id: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_RATING_REVIEW_URL)
    fun rateRestaurant(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiverId: String,
        @Field("rating") rating: String,
        @Field("review") review: String,
        @Field("receiver_type") receiver_type: String,
        @Field("order_id") orderId: String,
        @Field("good_review") goodReview: String,
        @Field("bad_review") badReview: String
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

    @GET(ApiConstants.TABLE_BOOKING_LIST)
    fun getBookedTables(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.SEARCH_USER)
    fun searchUser(
        @Header("Authorization") token: String,
        @Field("username") username: String

    ): Call<JsonElement>

    @GET(ApiConstants.GET_VOUCHER_CODES)
    fun getVoucherCodes(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_VOUCHER_CARD + "/{id}")
    fun getVoucherCode(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<JsonElement>


    @GET(ApiConstants.MY_CARDS)
    fun getCards(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.TRANSFER_MONEY)
    fun transferMoney(
        @Header("Authorization") token: String,
        @Field("username") walletId: String,
        @Field("amount") amount: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.REDEEM_CODE)
    fun redeem(
        @Header("Authorization") token: String,
        @Field("voucher_code") voucher_code: String

    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.SEND_GIFT)
    fun sendGift(
        @Header("Authorization") token: String,
        @Field("email") email: String,
        @Field("voucher_code") voucher_code: String,
        @Field("payment_method") payment_method: String,
        @Field("reference") reference: String

    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.GET_ALL_CARTS)
    fun getAllCarts(
        @Header("Authorization") token: String,
        @Field("device_id") device_id: String


    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.CHANGE_ANONOMOUS_TO_LOGIN)
    fun mergeCarts(
        @Header("Authorization") token: String,
        @Field("device_id") device_id: String,
        @Field("selected_cart_id") selected_cart_id: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.BUY_CARD)
    fun buyCard(
        @Header("Authorization") token: String,
        @Field("voucher_code") voucher_code: String,
        @Field("payment_method") payment_method: String,
        @Field("reference") reference: String

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


    @FormUrlEncoded
    @POST(ApiConstants.PICKUP_RESTAURANTS)
    fun getPickupRestaurants(
        @Header("Authorization") token: String,
        @Field("user_id") uid: String,
        @Field("latitude") latitude: String,
        @Field("longitude") logitude: String
    ): Call<JsonElement>

    @GET(ApiConstants.NOTIFICATIONS_LIST)
    fun getNotifications(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.CHANGE_DELIVERY_TO_PICKUP)
    fun changeDeliveryToPickup(
        @Header("Authorization") token: String,
        @Field("order_id") order_id: String,
        @Field("status") status: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.CREATE_GROUP_CART)
    fun createGroupOrder(
        @Header("Authorization") token: String,

        @Field("max_per_person") max_per_person: String,
        @Field("restaurant_id") restaurant_id: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_GROUP_ORDERS)
    fun getAllGroupOrders(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @GET(ApiConstants.GET_CHT_HEADS)
    fun getChatHeads(
        @Header("Authorization") token: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.SAVE_CART_LINK)
    fun saveCartLink(
        @Header("Authorization") token: String,
        @Field("cart_id") cart_id: String,
        @Field("share_link") share_link: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.ADD_IN_GROUP_CART)
    fun addInGroupCart(
        @Header("Authorization") token: String,
        @Field("cart_id") cart_id: String,
        @Field("share_link") share_link: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.SHOW_ALL_FILTER_DATA)
    fun showFilterData(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("filter_type") filter_type: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.RE_ORDER)
    fun reOrder(
        @Header("Authorization") token: String,
        @Field("order_id") order_id: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.DELETE_NOTIFICATION)
    fun deleteNotification(
        @Header("Authorization") token: String,
        @Field("notification_id") notificationId: String
    ): Call<JsonElement>


    @GET(ApiConstants.FAQ)
    fun getFaq(
        @Header("Authorization") token: String
    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GET_SUB_ISSUES)
    fun getSubIssues(
        @Header("Authorization") token: String,
        @Field("issue_id") issue_id: String

    ): Call<JsonElement>

    @FormUrlEncoded
    @POST(ApiConstants.GET_CHAT)
    fun getChat(
        @Header("Authorization") token: String,
        @Field("ticket_id") tiketId: String

    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.SEND_MESSGAE)
    fun sendMessage(
        @Header("Authorization") token: String,
        @Field("issue_id") issue_id: String,
        @Field("subissue_id") subissue_id: String,
        @Field("ticket_id") ticket_id: String,
        @Field("message") message: String
    ): Call<JsonElement>


    @FormUrlEncoded
    @POST(ApiConstants.TRENDING_MEALS)
    fun trendingMeals(
        @Header("Authorization") token: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("user_id") uid: String
    ): Call<JsonElement>


    @GET()
    fun getUpdatedTime(
        @Url url: String

    ): Call<JsonElement>
//"https://maps.googleapis.com/maps/api/directions/json?origin=\(source.latitude),\(source.longitude)&destination=\(destination.latitude),\(destination.longitude)&sensor=false&mode=driving&key=\(GoogleApiKey)")!

}

//   fSgph9c6OcQ:APA91bHI1DGyEOIWtpJEaHshDJ_2xSD2eT1UJmZVaGMr3eNTDS71DTKOyg9nPiiCvWJCR99Hw-TrDaY-Fi1m39jLc7W2mA0OOtUQpG2R4sdHyAgq5Y111_j_2IhBh1yFdPSO-rOiRL0j
