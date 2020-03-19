package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.Faq
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.view.ui.notifications.Notification

class FaqAdapter(
    val list: ArrayList<Notification>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TITLE = 1
    val BODY = 2

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is FaqModel) TITLE else BODY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == BODY) QAView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_faq,
                parent,
                false
            )
        ) else TitleView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_title_,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class QAView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_answer: TextView = view.findViewById(R.id.tv_answer)
        var tv_question: TextView = view.findViewById(R.id.tv_question)
    }

    inner class TitleView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleView) {
            holder
            var data = list[position] as FaqModel

            holder.tv_title.text = if (GrigoraApp.getInstance()
                    .getCurrentLanguage() == AppConstants.FRENCH
            ) data.french_name else data.name
        } else {
            holder as QAView
            var data = list[position] as Faq
            holder.tv_question.text = if (GrigoraApp.getInstance()
                    .getCurrentLanguage() == AppConstants.FRENCH
            ) data.french_question else data.question
            holder.tv_answer.text = if (GrigoraApp.getInstance()
                    .getCurrentLanguage() == AppConstants.FRENCH
            ) data.french_answer else data.answer
        }


    }

}