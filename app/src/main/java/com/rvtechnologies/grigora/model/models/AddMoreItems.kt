package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AddMoreItems(
    @SerializedName("approx_prep_time")
    var approxPrepTime: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("cuisine_id")
    var cuisineId: Int = 0,
    @SerializedName("description")
    var description: String = "",
    @SerializedName("french_description")
    var frenchDescription: String = "",
    @SerializedName("french_name")
    var frenchName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("in_offer")
    var inOffer: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("offer_price")
    var offerPrice: String = "",
    @SerializedName("price")
    var price: String = "",
    @SerializedName("pure_veg")
    var pureVeg: String = "",
    @SerializedName("restaurant_id")
    var restaurantId: Int = 0,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""



):Parcelable