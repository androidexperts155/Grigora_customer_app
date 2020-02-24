package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.model.models.ChipModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

class FaqAdapter(
    val list: ArrayList<FaqModel>
) :
    RecyclerView.Adapter<FaqAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_faq,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_answer: TextView = view.findViewById(R.id.tv_answer)
        var tv_question: TextView = view.findViewById(R.id.tv_question)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_question.text = list[position].question
        holder.tv_answer.text = list[position].answer

    }

}