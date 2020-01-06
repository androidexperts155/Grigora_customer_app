package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@SuppressLint("ParcelCreator")
@Parcelize
data class ItemCategory(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("item_id")
    var itemId: Int?,
    @SerializedName("item_sub_category")
    var itemSubCategory: List<ItemSubCategory?>?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("selection")
    var selection: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
):Parcelable