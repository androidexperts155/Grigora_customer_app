package com.rvtechnologies.grigora


import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.notifications.Notification

data class NotificationsModel(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("french_notification")
    var frenchNotification: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("notification")
    var notification: String = "",
    @SerializedName("notification_type")
    var notificationType: String = "",
    @SerializedName("read")
    var read: String = "",
    @SerializedName("role")
    var role: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
):Notification