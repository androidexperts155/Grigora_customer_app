package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryResponse(
    @SerializedName("data")
    var `data`: List<CategoryModel?>?,
    @SerializedName("current_page")
    var currentPage: Int?,
    @SerializedName("first_page_url")
    var firstPageUrl: String?,
    @SerializedName("from")
    var from: Int?,
    @SerializedName("last_page")
    var lastPage: Int?,
    @SerializedName("last_page_url")
    var lastPageUrl: String?,
    @SerializedName("next_page_url")
    var nextPageUrl: String?,
    @SerializedName("path")
    var path: String?,
    @SerializedName("per_page")
    var perPage: String?,
    @SerializedName("prev_page_url")
    var prevPageUrl: String?,
    @SerializedName("to")
    var to: Int?,
    @SerializedName("total")
    var total: Int?
):Parcelable