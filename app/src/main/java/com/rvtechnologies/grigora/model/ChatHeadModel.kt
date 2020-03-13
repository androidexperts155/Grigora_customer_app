package com.rvtechnologies.grigora.model

data class ChatHeadModel(
    val created_at: String,
    val id: Int,
    val issue_id: Int,
    val last_message: String,
    val message_type: String,
    val reciever_id: Int,
    val sender_id: Int,
    val subissue_id: Int,
    val ticket_id: String,
    val updated_at: String,
    val issue_name: String

)