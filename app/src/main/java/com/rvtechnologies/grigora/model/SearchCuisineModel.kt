package com.rvtechnologies.grigora.model

import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.search.SearchItem

class SearchCuisineModel(
@SerializedName("id")
var id: Int,
@SerializedName("name")
var name: String,
@SerializedName("french_name")
var french_name: String,
@SerializedName("image")
var image: String
): SearchItem