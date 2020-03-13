package com.rvtechnologies.grigora.model

data class VoucherCardCode(
    val amount: Int,
    val created_at: String,
    val id: Int,
    val updated_at: String,
    val valid: String,
    val voucher_code: String,
    val voucher_id: String
)