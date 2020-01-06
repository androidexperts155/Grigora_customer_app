package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class SearchUserModel(
    @SerializedName("address")
    var address: Any = Any(),
    @SerializedName("approved")
    var approved: String = "",
    @SerializedName("busy_status")
    var busyStatus: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    var deviceType: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("email_verified_at")
    var emailVerifiedAt: Any = Any(),
    @SerializedName("french_address")
    var frenchAddress: Any = Any(),
    @SerializedName("french_name")
    var frenchName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("id_proof")
    var idProof: String = "",
    @SerializedName("image")
    var image: String = "",
    @SerializedName("language")
    var language: String = "",
    @SerializedName("latitude")
    var latitude: Any = Any(),
    @SerializedName("longitude")
    var longitude: Any = Any(),
    @SerializedName("name")
    var name: String = "",
    @SerializedName("notification")
    var notification: String = "",
    @SerializedName("offer")
    var offer: Int = 0,
    @SerializedName("otp")
    var otp: Any = Any(),
    @SerializedName("otp_expire_time")
    var otpExpireTime: Any = Any(),
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("promo_id")
    var promoId: Any = Any(),
    @SerializedName("pure_veg")
    var pureVeg: String = "",
    @SerializedName("receipt_id")
    var receiptId: Any = Any(),
    @SerializedName("role")
    var role: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("wallet")
    var wallet: String = ""
)