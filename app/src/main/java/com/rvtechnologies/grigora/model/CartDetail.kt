package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.view.ui.groupCart.GroupCartType

data class CartDetail(
    val cart: List<CartDetail>,
    val french_name: String,
    val id: Int,
    val name: String
): GroupCartType