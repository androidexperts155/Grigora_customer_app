package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class User(
    @SerializedName("address")
    var address: String?,

    @SerializedName("security_pin")
    var pin: String?,

    @SerializedName("approved")
    var approved: String?,
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
    @SerializedName("id")
    var id: String?,
    @SerializedName("id_proof")
    var idProof: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("long")
    var long: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("otp")
    var otp: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("role")
    var role: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("have_address")
    var have_address: Boolean?

) : Parcelable