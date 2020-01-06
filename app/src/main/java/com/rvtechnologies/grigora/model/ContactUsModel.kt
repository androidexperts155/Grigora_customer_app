package com.rvtechnologies.grigora.model

 import com.google.gson.annotations.SerializedName


data class ContactUsModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)