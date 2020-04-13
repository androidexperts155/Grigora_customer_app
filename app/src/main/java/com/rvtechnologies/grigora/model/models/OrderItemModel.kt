package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderItemModel(
    @SerializedName("app_fee")
    var appFee: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",



    @SerializedName("order_type")
    var orderType: String = "",

    @SerializedName("group_order")
    var groupOrder: String = "",


    @SerializedName("delivery_address")
    var deliveryAddress: String = "",
    @SerializedName("delivery_fee")
    var deliveryFee: String = "",
    @SerializedName("delivery_note")
    var deliveryNote: String = "",
    @SerializedName("delivery_time")
    var deliveryTime: String = "",




    @SerializedName("dispatch")
    var dispatch: String = "",
    @SerializedName("driver_email")
    var driverEmail: String = "",
    @SerializedName("driver_id")
    var driverId: String = "",
    @SerializedName("driver_image")
    var driverImage: String = "",
    @SerializedName("driver_lat")
    var driverLat: String = "",
    @SerializedName("driver_long")
    var driverLong: String = "",
    @SerializedName("driver_name")
    var driverName: String = "",
    @SerializedName("driver_phone")
    var driverPhone: String = "",
    @SerializedName("end_lat")
    var endLat: Double = 0.0,
    @SerializedName("end_long")
    var endLong: Double = 0.0,
    @SerializedName("final_price")
    var finalPrice: String = "",
    var finalPriceToShow: String = "",

    @SerializedName("restaurant_cusines")
    var restaurant_cusines: String = "",


    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_schedule")
    var isSchedule: String = "",
    @SerializedName("order_details")
    var orderDetails: ArrayList<OrderDetail> = ArrayList(),
    @SerializedName("order_status")
    var orderStatus: Int?,
    @SerializedName("payment_data")
    var paymentData: String = "",
    @SerializedName("payment_method")
    var paymentMethod: String = "",
    @SerializedName("preparing_end_time")
    var preparingEndTime: String = "",
    @SerializedName("preparing_time")
    var preparingTime: String = "",



    @SerializedName("price_after_promo")
    var priceAfterPromo: String = "",
    @SerializedName("price_before_promo")
    var priceBeforePromo: String = "",
    @SerializedName("promocode")
    var promocode: String = "",
    @SerializedName("quantity")
    var quantity: Int = 0,
    @SerializedName("reference")
    var reference: String = "",
    @SerializedName("request_time")
    var requestTime: String = "",
    @SerializedName("restaurant_email")
    var restaurantEmail: String = "",
    @SerializedName("restaurant_id")
    var restaurantId: Int = 0,
    @SerializedName("restaurant_image")
    var restaurantImage: String = "",
    @SerializedName("restaurant_lat")
    var restaurantLat: String = "",
    @SerializedName("restaurant_long")
    var restaurantLong: String = "",
    @SerializedName("restaurant_name")
    var restaurantName: String = "",
    @SerializedName("restaurant_phone")
    var restaurantPhone: String = "",
    @SerializedName("schedule_time")
    var scheduleTime: String = "",
    @SerializedName("start_address")
    var startAddress: String = "",
    @SerializedName("start_lat")
    var startLat: Double = 0.0,
    @SerializedName("start_long")
    var startLong: Double = 0.0,
    @SerializedName("time_remaining")
    var timeRemaining: String = "",

    @SerializedName("is_driver_rated")
    var is_driver_rated: String = "",

    @SerializedName("is_restaurant_rated")
    var is_restaurant_rated: String = "",


     var isReorder: Boolean= false,


    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_email")
    var userEmail: String = "",
    @SerializedName("user_id")
    var userId: Int = 0,
    @SerializedName("user_image")
    var userImage: String = "",
    @SerializedName("user_name")
    var userName: String = "",
    @SerializedName("user_phone")
    var userPhone: String = "",
    var idToShow: String,
    var is_rated: Boolean = false,
    var is_already_rated: Boolean = false,
    var status: String = "",
    var rating:Float= 0.0F


) : Parcelable {


    @SuppressLint("ParcelCreator")
    @Parcelize
    data class OrderDetail(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: Int = 0,

        @SerializedName("veg")
        @Expose
        var pureVeg: String?,

        @SerializedName("item_choices")
        var itemChoices: ArrayList<ItemChoice> = ArrayList(),
        @SerializedName("item_french_name")
        var itemFrenchName: String = "",
        @SerializedName("item_id")
        var itemId: Int = 0,
        @SerializedName("item_name")
        var itemName: String = "",
        @SerializedName("order_id")
        var orderId: Int = 0,
        @SerializedName("price")
        var price: String = "",
        @SerializedName("quantity")
        var quantity: Int = 0,
        @SerializedName("is_item_rated")
        var is_item_rated: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = "",
        var orderItem: String,
        var rating: Float=0.0F,
        var review: String=""

    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        data class ItemChoice(
            @SerializedName("french_name")
            var frenchName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("item_sub_category")
            var itemSubCategory: List<ItemSubCategory> = listOf(),
            @SerializedName("name")
            var name: String = "",
            @SerializedName("selection")
            var selection: String = ""
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            data class ItemSubCategory(
                @SerializedName("add_on_price")
                var addOnPrice: String = "",
                @SerializedName("french_name")
                var frenchName: String = "",

                @SerializedName("item_sub_sub_category")
                var item_sub_sub_category: List<RestaurantDetailNewModel.ItemSubSubCategory> = listOf(),

                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            ) : Parcelable
        }
    }
}