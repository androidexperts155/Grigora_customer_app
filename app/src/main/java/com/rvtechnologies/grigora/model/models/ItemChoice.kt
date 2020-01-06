package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ItemChoice(
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("item_sub_category")
    var itemSubCategory: List<ItemSubCategory?>?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("selection")
    var selection: String?
):Parcelable