package com.rvtechnologies.grigora.model

data class ReceivedVoucherModel(
    val amount: String,
    val created_at: String,
    val id: Int,
    val redemed: String,
    val sender_id: Int,
    val updated_at: String,
    val user_id: Int,
    val voucher_code: String
)