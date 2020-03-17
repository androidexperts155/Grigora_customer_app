package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.view.ui.notifications.Notification

data class MyPurchasedCardModel(
    val amount: String,
    val created_at: String,
    val id: Int,
    val redemed: String,
    val sender_id: Int,
    val updated_at: String,
    val voucher_image: String,
    val user_id: Int,
    var isShare: Boolean,
    val voucher_code: String

): Notification