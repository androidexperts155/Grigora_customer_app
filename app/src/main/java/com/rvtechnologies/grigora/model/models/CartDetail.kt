package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CartDetail(
    @SerializedName("cart_id")
    var cartId: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("item_french_name")
    var itemFrenchName: String?,
    @SerializedName("item_id")
    var itemId: String?,
    @SerializedName("item_name")
    var itemName: String?,
    @SerializedName("price")
    var price: String?,
    @SerializedName("quantity")
    var quantity: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("item_choices")
    var item_choices: ArrayList<ItemChoice>?,
    @Transient
    var itemNameToDisplay: String,
    var choicesString: String,
    var total: String?

):Parcelable