package com.rvtechnologies.grigora.view.ui.restaurant_list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.view_model.GroupOrderViewModel

class GroupOrderFragment : Fragment() {

    companion object {
        fun newInstance() = GroupOrderFragment()
    }

    private lateinit var viewModel: GroupOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.group_order_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GroupOrderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
