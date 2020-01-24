package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class RestaurantModel(

    @SerializedName("address")
    var address: String?,

    @SerializedName("approved")
    var approved: String?,
    @SerializedName("average_rating")
    var avgRatings: Float?,
    @SerializedName("total_rating")
    var rateCount: Int?,
    @SerializedName("busy_status")
    var busyStatus: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("cuisines")
    var cuisines: String?,
    @SerializedName("device_token")
    var deviceToken: String?,
    @SerializedName("device_type")
    var deviceType: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("email_verified_at")
    var emailVerifiedAt: String?,
    @SerializedName("favourite")
    var favourite: Boolean,
    @SerializedName("french_address")
    var frenchAddress: String?,
    @SerializedName("french_name")
    var frenchName: String?,

    var nameToShow: String?,
    var addressToShow: String?,

    @SerializedName("id")
    var id: String?,
    @SerializedName("id_proof")
    var idProof: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("language")
    var language: String?,
    @SerializedName("latitude")
    var latitude: Double? = 0.0,
    @SerializedName("longitude")
    var longitude: Double? = 0.0,
    @SerializedName("name")
    var name: String?,
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
    var wallet: String?,
    var avgRatingsString: String = "0.0",
    var fav: Boolean?

) : Parcelable