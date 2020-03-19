package com.rvtechnologies.grigora.view.ui.contact_us

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ContactUsFragmentBinding
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.FaqAdapter
import com.rvtechnologies.grigora.view.ui.notifications.Notification
import com.rvtechnologies.grigora.view_model.ContactUsViewModel
import kotlinx.android.synthetic.main.contact_us_fragment.*

class ContactUs : Fragment() {
    private lateinit var viewModel: ContactUsViewModel
    private var contactUsFragmentBinding: ContactUsFragmentBinding? = null
    var subIssues = ArrayList<SubIssueModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)

        viewModel.faqRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var list = ArrayList<FaqModel>()
                    list.addAll(response.data as Collection<FaqModel>)
                    tv_viewall.visibility = View.VISIBLE
                    var list1 = ArrayList<Notification>()
                    list1.add(list[0].faqs[0])
                    list1.add(list[0].faqs[1])
                    rc_faqs.adapter = FaqAdapter(list1)
                }
            }
        })

        viewModel.subIssuesRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    subIssues.addAll(response.data as Collection<SubIssueModel>)
                }
            }
        })



        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

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

        viewModel.getFaq(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
        viewModel.getSubIssues(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))

    }

    fun viewAll() {
        view?.findNavController()
            ?.navigate(R.id.action_contactUs_to_all_faq)
    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }

    fun chats() {
        view?.findNavController()?.navigate(R.id.action_contactUs_to_chatHeads)
    }

    fun i1() {
        if (hasSubIssues("1")) {
            var bundle = bundleOf(
                AppConstants.ISSUE_ID to "1",
                AppConstants.TITLE to getString(R.string.issue_order)
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_subissues, bundle)
        } else {
            var bundle = bundleOf(
                AppConstants.ISSUE_ID to "1",
                AppConstants.SUB_ISSUE_ID to "",
                AppConstants.TICKET_ID to ""
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_chat, bundle)
        }
    }

    fun i2() {
        if (hasSubIssues("2")) {
            var bundle = bundleOf(
                AppConstants.TITLE to getString(R.string.gora_pouch),
                AppConstants.ISSUE_ID to "2"
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_subissues, bundle)
        } else {
            var bundle = bundleOf(
                AppConstants.ISSUE_ID to "2",
                AppConstants.SUB_ISSUE_ID to "",
                AppConstants.TICKET_ID to ""
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_chat, bundle)
        }
    }

    fun i3() {
        if (hasSubIssues("3")) {
            var bundle = bundleOf(
                AppConstants.TITLE to getString(R.string.fraud_issue),
                AppConstants.ISSUE_ID to "3"
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_subissues, bundle)
        } else {
            var bundle = bundleOf(
                AppConstants.ISSUE_ID to "3",
                AppConstants.SUB_ISSUE_ID to "",
                AppConstants.TICKET_ID to ""
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_chat, bundle)
        }
    }

    fun i4() {
        if (hasSubIssues("4")) {
            var bundle = bundleOf(
                AppConstants.TITLE to getString(R.string.others),
                AppConstants.ISSUE_ID to "4"
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_subissues, bundle)
        } else {
            var bundle = bundleOf(
                AppConstants.ISSUE_ID to "4",
                AppConstants.SUB_ISSUE_ID to "",
                AppConstants.TICKET_ID to ""
            )
            view?.findNavController()?.navigate(R.id.action_contactUs_to_all_chat, bundle)
        }
    }

    private fun hasSubIssues(id: String): Boolean {
        var has = false
        for (item in subIssues) {
            if (item.issue_id == id.toInt()) {
                has = true
                break
            }
        }
        return has
    }
}
