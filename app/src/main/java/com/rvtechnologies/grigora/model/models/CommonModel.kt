package com.rvtechnologies.grigora.model.models

import com.google.gson.annotations.SerializedName


data class CommonModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)