package com.rvtechnologies.grigora.model.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class LoginResponseModel(
    @SerializedName("data")
    var `data`: User?,
    @SerializedName("access_token")
    var accessToken: String?,
    @SerializedName("message")
    var message: String?,

    @SerializedName("user_id")
    var user_id: String?,

    @SerializedName("phone")
    var phone: String?,


    @SerializedName("status")
    var status: Boolean?,

    @SerializedName("phone_verified")
    var phone_verified: Boolean?,

    @SerializedName("email_verified")
    var email_verified: Boolean?,

    @SerializedName("token_type")
    var tokenType: String?
):Parcelable

