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
import com.rvtechnologies.grigora.model.ChatHeadModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.ChatHeadsAdapter
import com.rvtechnologies.grigora.view_model.ChatHeadsViewModel
import kotlinx.android.synthetic.main.chat_heads_fragment.*

class ChatHeads : Fragment(), IRecyclerItemClick {
    companion object {
        fun newInstance() = ChatHeads()
    }

    var headsList = ArrayList<ChatHeadModel>()
    private lateinit var viewModel: ChatHeadsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatHeadsViewModel::class.java)

        viewModel.subIssuesRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    headsList.clear()
                    headsList.addAll(res.data as ArrayList<ChatHeadModel>)
                    rc_chat_heads.adapter = ChatHeadsAdapter(headsList, this)

                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_heads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.chat_heads))

        viewModel.getChatHeads(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }

    override fun onItemClick(item: Any) {
        item as ChatHeadModel
        var bundle = bundleOf(
            AppConstants.ISSUE_ID to item.issue_id, AppConstants.SUB_ISSUE_ID to item.subissue_id,
            AppConstants.TICKET_ID to item.ticket_id
        )

        view?.findNavController()?.navigate(R.id.action_chatHeads_to_all_chat, bundle)
    }
}
