package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class NotificationDataModel(
    @SerializedName("notification_type")
    var notificationType: String = "",
    @SerializedName("order_id")
    var orderId: String = "",
    @SerializedName("restaurant_address")
    var restaurantAddress: String = "",
    @SerializedName("restaurant_image")
    var restaurantImage: String = "",
    @SerializedName("restaurant_lat")
    var restaurantLat: String = "",
    @SerializedName("restaurant_long")
    var restaurantLong: String = "",
    @SerializedName("restaurant_name")
    var restaurantName: String = "",
    @SerializedName("type")
    var type: String = "",


    @SerializedName("restaurant_preparing_time")
    var restaurant_preparing_time: Int = 0,

    @SerializedName("preparing_time")
    var preparing_time: Int = 0,


    var message: String = ""


)