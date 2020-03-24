package com.rvtechnologies.grigora


import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.notifications.Notification

data class NotificationsModel(
    @SerializedName("created_at")
    var createdAt: String = "",

    @SerializedName("order_id")
    var orderId: Int = 0,


    @SerializedName("french_notification")
    var frenchNotification: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("notification")
    var notification: String = "",
    @SerializedName("notification_type")
    var notificationType: Int = 0,
    @SerializedName("table_booking")
    var tableBooking: Int = 0,

    @SerializedName("read")
    var read: String = "",
    @SerializedName("role")
    var role: String = "",


    @SerializedName("updated_at")
    var updatedAt: String = "",

    @SerializedName("pickup")
    var pickup: String = "",


    @SerializedName("no_of_seats")
    var no_of_seats: String = "",

    @SerializedName("opening_time")
    var opening_time: String = "",

    @SerializedName("closing_time")
    var closing_time: String = "",

    @SerializedName("full_time")
    var full_time: String = "",


    @SerializedName("user_id")
    var userId: Int = 0,

    @SerializedName("timer")
    var timer: Int = 0,

    @SerializedName("restaurant_id")
    var restautrantId: Int = 0,


    var timeToShow: String = ""
) : Notification