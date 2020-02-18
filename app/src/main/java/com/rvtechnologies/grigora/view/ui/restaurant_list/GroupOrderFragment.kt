package com.rvtechnologies.grigora.view.ui.restaurant_list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.GroupOrderFragmentBinding
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view_model.GroupOrderViewModel
import kotlinx.android.synthetic.main.group_order_fragment.*

class GroupOrderFragment : Fragment(), IRecyclerItemClick {

    private lateinit var groupOrderFragmentBinding: GroupOrderFragmentBinding
    var selected_amount = ""

    companion object {
        fun newInstance() = GroupOrderFragment()
    }

    private lateinit var viewModel: GroupOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupOrderFragmentBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.group_order_fragment,
                container,
                false
            )
        groupOrderFragmentBinding.orderView = this
        return groupOrderFragmentBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GroupOrderViewModel::class.java)
    }

    fun none() {
        selected_amount = ""
        tv_none.setBackgroundResource(R.drawable.none_sel)
        tv_none.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)


    }

    fun p1() {
        selected_amount = "10"
        tv_p1.setBackgroundResource(R.drawable.price_sel)
        tv_p1.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_none.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p2() {
        selected_amount = "15"

        tv_p2.setBackgroundResource(R.drawable.price_sel)
        tv_p2.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_none.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p3() {
        selected_amount = "20"

        tv_p3.setBackgroundResource(R.drawable.price_sel)
        tv_p3.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_none.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p4() {
        selected_amount = "25"

        tv_p4.setBackgroundResource(R.drawable.price_sel)
        tv_p4.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_none.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun moreInfo() {
        var groupOrderInfoDialog = GroupOrderInfoDialog()
        groupOrderInfoDialog.show(this.childFragmentManager, "")
    }

    fun other() {

        tv_others.setBackgroundResource(R.drawable.others_sel)
        tv_others.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_none.setTextColor(color)
        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        var otherAmount = OtherAmount(this)
        otherAmount.show(this.childFragmentManager, "")
    }

    override fun onItemClick(item: Any) {
        if (item is String) {
            selected_amount = item

            tv_others.text = "₦".plus(item)
        }

    }


}
