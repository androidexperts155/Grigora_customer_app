package com.rvtechnologies.grigora.model

data class MessageModel(
    val date: String="",
    val issue_id: Int=0,
    val message: String="",
    val sender_id: Int=0,
    val subissue_id: Int=0,
    val ticket_id: String=""
)