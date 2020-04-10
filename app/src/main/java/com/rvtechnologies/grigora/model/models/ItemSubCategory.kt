package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ItemSubCategory(
    @SerializedName("add_on_price")
    var addOnPrice: Double=0.0,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("french_name")
    var frenchName: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("item_cat_id")
    var itemCatId: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @Transient
    var checked: Boolean=false,

    @SerializedName("item_sub_sub_category")
    var item_sub_sub_category: List<RestaurantDetailNewModel.ItemSubSubCategory?>?,


    @Transient
    var addOnPriceString: String=""
): Parcelable