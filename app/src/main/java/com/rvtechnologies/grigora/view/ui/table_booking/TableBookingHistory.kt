package com.rvtechnologies.grigora.view.ui.table_booking

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.facebook.common.Common
import com.google.android.material.tabs.TabLayout

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.TableBookingHistoryModel
import com.rvtechnologies.grigora.model.TableBookingModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.table_booking_history_fragment.*

class TableBookingHistory : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = TableBookingHistory()
    }

    private var history = ArrayList<TableBookingHistoryModel>()
    private lateinit var viewModel: TableBookingHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TableBookingHistoryViewModel::class.java)
        viewModel?.isLoading?.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })
        viewModel.tableBookingList.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                history = res.data as ArrayList<TableBookingHistoryModel>
                rec_history.adapter = BookingAdapter(history, this)
            } else {
                CommonUtils.showMessage(parent, res.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.table_booking_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 0 until tab_top.tabCount) {

            //noinspection ConstantConditions
            var tv = layoutInflater.inflate(R.layout.tab_textview, null) as TextView
            tv.gravity = Gravity.CENTER
            tv.text = tab_top.getTabAt(i)?.text

            if (tab_top.getTabAt(i)?.isSelected!!) {
                tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            } else {
                if (CommonUtils.isDarkMode())
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }
            tab_top.getTabAt(i)?.customView = tv
        }
        tabListener()
    }

    fun tabListener() {
        tab_top.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {


            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView

                view as TextView

                if (CommonUtils.isDarkMode())
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView
                view as TextView
                view.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

                when {
                    p0!!.text.toString() == getString(R.string.all) -> {
                        filterList(0)
                    }
                    p0!!.text.toString() == getString(R.string.approved) -> {
                        filterList(2)
                    }
                    p0!!.text.toString() == getString(R.string.pending) -> {
                        filterList(1)


                    }
                }
            }
        })
    }

    fun filterList(status: Int) {
        var temp = ArrayList<TableBookingHistoryModel>()

        if (status == 0)
            temp.addAll(history)
        else
            for (item in history)
                if (item.booking_status == status)
                    temp.add(item)


        rec_history.adapter = BookingAdapter(temp, this)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.table_booking))
        viewModel.getList(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }

    override fun onItemClick(item: Any) {
        if (item is TableBookingHistoryModel) {
            var dialog = BookingDetailDialog(item)
            dialog.show(childFragmentManager, "")
        }
    }

}
