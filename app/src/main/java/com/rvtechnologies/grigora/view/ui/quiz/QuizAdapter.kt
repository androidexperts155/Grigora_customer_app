package com.rvtechnologies.grigora.view.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.QuizModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class QuizAdapter(val model: QuizModel, val iRecyclerItemClick: IRecyclerItemClick) :
    RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        return QuizViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_answer,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return model.options.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
//        check if selected
        holder.tv_name.text=model.options[position]

        if (model.selected == position) {
            holder.img_check.visibility = View.VISIBLE
            holder.rel_sel.setBackgroundResource(R.drawable.green_sel)
            holder.li_main.setBackgroundResource(R.drawable.answer_selected)
        } else {
            holder.img_check.visibility = View.GONE
            holder.rel_sel.setBackgroundResource(0)
            holder.li_main.setBackgroundResource(R.drawable.answer_de_selected)
        }

        if (CommonUtils.isDarkMode())
            holder.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.tv_name.context!!,
                    R.color.white
                )
            )
        else
            holder.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.tv_name.context!!,
                    R.color.textGrey
                )
            )


        holder.li_main.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }

    }


    inner class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var li_main = view.findViewById<LinearLayout>(R.id.li_main)
        var rel_sel = view.findViewById<RelativeLayout>(R.id.rel_sel)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var img_check = view.findViewById<ImageView>(R.id.img_check)
    }

}