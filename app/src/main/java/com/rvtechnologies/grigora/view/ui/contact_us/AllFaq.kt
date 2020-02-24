package com.rvtechnologies.grigora.view.ui.contact_us


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.FaqAdapter
import com.rvtechnologies.grigora.view_model.ContactUsViewModel
import kotlinx.android.synthetic.main.contact_us_fragment.*
import kotlinx.android.synthetic.main.fragment_all_faq.*
import kotlinx.android.synthetic.main.fragment_all_faq.rc_faqs


class AllFaq : Fragment() {
    private lateinit var viewModel: ContactUsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_faq, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)

        viewModel.faqRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var list = ArrayList<FaqModel>()
                    list.addAll(response.data as Collection<FaqModel>)
                    rc_faqs.adapter = FaqAdapter(list)
                }
            }
        })
        viewModel.getFaq(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.faq))
    }

}
