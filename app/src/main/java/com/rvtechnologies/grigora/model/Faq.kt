package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.view.ui.notifications.Notification

data class Faq(
    val answer: String,
    val created_at: String,
    val faq_categoryid: Int,
    val french_answer: String,
    val french_question: String,
    val icon: Any,
    val id: Int,
    val question: String,
    val status: String,
    val updated_at: String
):Notification