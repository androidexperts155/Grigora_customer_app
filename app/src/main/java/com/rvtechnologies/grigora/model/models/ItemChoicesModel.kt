package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ItemChoicesModel(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("item_sub_category")
    var itemSubCategory: String?
):Parcelable