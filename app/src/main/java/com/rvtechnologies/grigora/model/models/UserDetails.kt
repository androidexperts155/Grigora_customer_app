package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UserDetails(
    @SerializedName("address")
    var address: String?,
    @SerializedName("approved")
    var approved: String?,
    @SerializedName("busy_status")
    var busyStatus: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("device_token")
    var deviceToken: String?,
    @SerializedName("device_type")
    var deviceType: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("email_verified_at")
    var emailVerifiedAt: String?,
    @SerializedName("french_address")
    var frenchAddress: String?,
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("id_proof")
    var idProof: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("language")
    var language: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("offer")
    var offer: String?,
    @SerializedName("otp")
    var otp: String?,
    @SerializedName("otp_expire_time")
    var otpExpireTime: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("promo_id")
    var promoId: String?,
    @SerializedName("pure_veg")
    var pureVeg: String?,
    @SerializedName("role")
    var role: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("wallet")
    var wallet: String?
) : Parcelable