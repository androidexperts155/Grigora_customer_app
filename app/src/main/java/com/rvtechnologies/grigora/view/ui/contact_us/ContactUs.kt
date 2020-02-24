package com.rvtechnologies.grigora.view.ui.contact_us

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ContactUsFragmentBinding
import com.rvtechnologies.grigora.databinding.ProfileDetailsFragmentBinding
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.FaqAdapter
import com.rvtechnologies.grigora.view_model.ContactUsViewModel
import kotlinx.android.synthetic.main.contact_us_fragment.*

class ContactUs : Fragment() {
    private lateinit var viewModel: ContactUsViewModel
    private var contactUsFragmentBinding: ContactUsFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)

        viewModel.faqRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var list = ArrayList<FaqModel>()
                    list.addAll(response.data as Collection<FaqModel>)

                    if (list.size > 2) {
                        tv_viewall.visibility = View.VISIBLE
                    }
                    var list1 = ArrayList<FaqModel>()
                    list1.add(list[0])
                    list1.add(list[1])


                    rc_faqs.adapter = FaqAdapter(list1)
                }
            }
        })
        viewModel.getFaq(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contactUsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.contact_us_fragment,
            container,
            false
        ) as ContactUsFragmentBinding


        contactUsFragmentBinding?.contactUsView = this
        return contactUsFragmentBinding?.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
    }

    fun viewAll() {
        view?.findNavController()
            ?.navigate(R.id.action_contactUs_to_all_faq)
    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }
}
