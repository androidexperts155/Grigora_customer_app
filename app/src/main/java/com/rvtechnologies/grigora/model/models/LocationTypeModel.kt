package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class LocationTypeModel(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("french_description")
    var frenchDescription: String?,
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,var selected:Boolean=false
):Parcelable