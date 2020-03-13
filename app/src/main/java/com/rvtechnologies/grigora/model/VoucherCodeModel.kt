package com.rvtechnologies.grigora.model

data class VoucherCodeModel(
    val amount: Int,
    val created_at: String,
    val id: Int,
    val status: String,
    val updated_at: String,
    val voucher_card_code: VoucherCardCode,
    val voucher_image: String
)