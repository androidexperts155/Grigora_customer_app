package com.rvtechnologies.grigora.view.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.rvtechnologies.grigora.R

class ConatctTypeAdapter(var type: ArrayList<String>) : BaseAdapter()  {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.spinner_custom, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh

        } else {
            view = convertView
            vh = view.tag as ItemRowHolder

        }
        vh.label.text = type.get(position)

        return view
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return type.size
    }
    private class ItemRowHolder(row: View?) {
        val label: TextView
        init  {
            this.label = row?.findViewById(R.id.tv_custom) as TextView

        }

    }
}