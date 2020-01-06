package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class MenuItemModel(
    @SerializedName("id")
    @Expose
    var id: Int?,


    @SerializedName("total_rating")
    @Expose
    var total_rating: Int?,

    @SerializedName("item_count_in_cart")
    @Expose
    var item_count_in_cart: Int?,


    @SerializedName("approx_prep_time")
    @Expose
    var approx_prep_time: Int?,


    @SerializedName("restaurant_id")
    @Expose
    var restaurantId: Int?,

    @SerializedName("avg_ratings")
    @Expose
    var avg_ratings: Float?,


    @SerializedName("category_id")
    @Expose
    var categoryId: Int?,


    @SerializedName("name")
    @Expose
    var name: String?,


    @SerializedName("restaurant_name")
    @Expose
    var restaurant_name: String?,


    @SerializedName("favourite")
    @Expose
    var favourite: Boolean,


    @SerializedName("french_name")
    @Expose
    var frenchName: String?,
    @SerializedName("description")
    @Expose
    var description: String?,
    @SerializedName("french_description")
    @Expose
    var frenchDescription: String?,
    @SerializedName("image")
    @Expose
    var image: String?,
    @SerializedName("price")
    @Expose
    var price: String?,
    @SerializedName("offer_price")
    @Expose
    var offerPrice: String?,
    @SerializedName("in_offer")
    @Expose
    var inOffer: Int?,
    @SerializedName("restaurant_offer")
    @Expose
    var restaurant_offer: Int?,
    var offPercentage: String?,
    @SerializedName("pure_veg")
    @Expose
    var pureVeg: String?,
    @SerializedName("status")
    @Expose
    var status: String?,
    @SerializedName("created_at")
    @Expose
    var createdAt: String?,
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String?,
    @SerializedName("category_name")
    @Expose
    var categoryName: String?,
    @SerializedName("item_categories")
    @Expose
    var itemCategories: List<ItemCategory>?,

    var nameToShow: String?

) : Parcelable