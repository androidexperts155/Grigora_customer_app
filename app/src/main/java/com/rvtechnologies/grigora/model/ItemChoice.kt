package com.rvtechnologies.grigora.model

data class ItemChoice(
    val french_name: String,
    val id: Int,
    val item_sub_category: List<ItemSubCategory>,
    val name: String,
    val selection: String,
    val veg: String
)