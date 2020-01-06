package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class OfferModel(
    @SerializedName("code")
    var code: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("no_of_attempts")
    var noOfAttempts: Int?,
    @SerializedName("percentage")
    var percentage: Int?,

    @SerializedName("status")
    var status: String?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("french_description")
    var french_description: String?,

    @SerializedName("is_valid")
    var is_valid: Boolean?,


    @SerializedName("updated_at")
    var updatedAt: String?

) : Parcelable