package com.rvtechnologies.grigora.view.ui.contact_us

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.SubIssuesAdapter
import com.rvtechnologies.grigora.view_model.SubIssuesViewModel
import kotlinx.android.synthetic.main.sub_issues_fragment.*

class SubIssues : Fragment(),IRecyclerItemClick {

    companion object {
        fun newInstance() = SubIssues()
    }


    var subIssues = ArrayList<SubIssueModel>()


    private lateinit var viewModel: SubIssuesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SubIssuesViewModel::class.java)

        viewModel.subIssuesRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    subIssues.addAll(response.data as Collection<SubIssueModel>)

                    var tempIssues = ArrayList<SubIssueModel>()
                    for (item in subIssues) {
                        if (item.issue_id.toString() == arguments?.get(AppConstants.ISSUE_ID)!!
                                .toString()
                        ) {
                            tempIssues.add(item)
                        }
                    }
                    subIssues.clear()

                    subIssues.addAll(tempIssues)

                    rc_subissues.adapter=SubIssuesAdapter(subIssues,this)
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sub_issues_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(arguments?.get(AppConstants.TITLE).toString())
        viewModel.getSubIssues(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }

    override fun onItemClick(item: Any) {
        if(item is SubIssueModel){
            var bundle = bundleOf(AppConstants.ISSUE_ID to arguments?.get(AppConstants.ISSUE_ID)!!, AppConstants.SUB_ISSUE_ID to item.id,AppConstants.TICKET_ID to "")
            view?.findNavController()?.navigate(R.id.action_subIssues_to_all_chat, bundle)
        }
    }

}
