package com.rvtechnologies.grigora.view.ui.cart

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ScheduleOrderFragmentBinding
import com.rvtechnologies.grigora.model.DayModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.DayAdapter
import com.rvtechnologies.grigora.view_model.ScheduleOrderViewModel
import kotlinx.android.synthetic.main.schedule_order_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleOrder : Fragment(), IRecyclerItemClick {

    var daysList = ArrayList<DayModel>()
    private lateinit var viewModel: ScheduleOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScheduleOrderViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = DataBindingUtil.inflate(
            inflater,
            R.layout.schedule_order_fragment,
            container,
            false
        ) as ScheduleOrderFragmentBinding
        binding.scheduleView = this
        binding.scheduleOrderViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDates()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.schedule_your_order))

    }


    private fun setDates() {
        var date = Calendar.getInstance()

        daysList.add(
            DayModel(
                getString(R.string.today),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd"), true
            )
        )
        date.add(Calendar.DAY_OF_MONTH, 1)

        daysList.add(
            DayModel(
                getString(R.string.tomorrow),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd")
            )
        )

        date.add(Calendar.DAY_OF_MONTH, 1)
        daysList.add(
            DayModel(
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "EEE"),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd")
            )
        )

        date.add(Calendar.DAY_OF_MONTH, 1)
        daysList.add(
            DayModel(
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "EEE"),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd")
            )
        )


        rc_days.adapter = DayAdapter(daysList, this)

    }

    override fun onItemClick(item: Any) {
        item as Int
        var deSelected = 0

        for (i in 0.until(daysList.size)) {
            if (daysList[i].selected) {
                deSelected = i
            }
        }

        daysList[item].selected = true
        rc_days.adapter?.notifyItemChanged(item)
        daysList[deSelected].selected = false
        rc_days.adapter?.notifyItemChanged(deSelected)


    }
}
