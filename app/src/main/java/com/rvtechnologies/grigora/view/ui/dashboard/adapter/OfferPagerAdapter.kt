package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import androidx.viewpager.widget.PagerAdapter

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.OfferItemBinding
import com.rvtechnologies.grigora.model.models.OfferModel

class OfferPagerAdapter(
    var mContext: Context,
    var offerList: ArrayList<OfferModel>
) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return offerList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(mContext)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.offer_item, container,false) as OfferItemBinding

        val offer=offerList[position]
        binding.offerModel=offer
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}
