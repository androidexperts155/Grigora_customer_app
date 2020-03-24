package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ScheduleOrderDetailsViewModel

class ScheduleOrderDetails : Fragment() {
    companion object {
        fun newInstance() = ScheduleOrderDetails()
    }

    private lateinit var viewModel: ScheduleOrderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_order_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScheduleOrderDetailsViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.scheduled_order))

    }
}
