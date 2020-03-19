package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.view.ui.notifications.Notification

data class FaqModel(
    val created_at: String,
    val faqs: List<Faq>,
    val french_name: String,
    val id: Int,
    val name: String,
    val updated_at: String
): Notification