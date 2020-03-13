package com.rvtechnologies.grigora.view.ui.contact_us

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.MessageModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel1
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.contact_us.adapter.ChatAdapter
import com.rvtechnologies.grigora.view_model.ChatViewModel
import kotlinx.android.synthetic.main.chat_fragment.*

class ChatFragment : Fragment() {

    var list = ArrayList<MessageModel>()
    private var receiver: BroadcastReceiver? = null

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        viewModel.issueId.value = arguments?.get(AppConstants.ISSUE_ID).toString()
        viewModel.subIssueId.value = arguments?.get(AppConstants.SUB_ISSUE_ID).toString()
        viewModel.ticketId.value = arguments?.get(AppConstants.TICKET_ID).toString()
        viewModel.token.value = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)

        viewModel.sendMessageRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    var messageModel = res.data as MessageModel
                    if (messageModel != null) {
                        viewModel.ticketId.value = messageModel.ticket_id
//                        list.add(messageModel)
//                        rc_chat.adapter?.notifyItemInserted(list.size - 1)
                    }
                } else
                    CommonUtils.showMessage(parent, res.message!!)
            } else {
                CommonUtils.showMessage(parent, res.toString())
            }
        })
        viewModel.messages.observe(this, Observer { res ->
            if (res is CommonResponseModel1<*>) {
                if (res.status == 1) {
                    var messages = res.data as ArrayList<MessageModel>

                    messages.reverse()
                    if (messages.size != list.size || list.size == 0) {
                        list.clear()
                        list.addAll(messages)
                    }
//                    rc_chat.adapter?.notifyItemInserted(list.size - 1)
                    rc_chat.adapter?.notifyDataSetChanged()
                    rc_chat.scrollToPosition(list.size-1)
                } else
                    CommonUtils.showMessage(parent, res.message!!)
            } else {
                CommonUtils.showMessage(parent, res.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_send.setOnClickListener {
            send()
        }


        rc_chat.adapter = ChatAdapter(list)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()



        refreshChat()


        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1!!.getStringExtra(AppConstants.TICKET_ID).equals(viewModel.ticketId.value)) {
                    if (p1!!.getStringExtra(AppConstants.NOTIFICATION_TYPE).toInt() == 112) {


                    }
                }
            }
        }
        activity!!.registerReceiver(receiver, IntentFilter("com.rvtechnologies.grigora"))
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(receiver)
    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }

    fun send() {
        if (!ed_message.text.toString().isNullOrEmpty()) {
            viewModel.sendMessage(ed_message.text.toString())
            list.add(
                MessageModel(
                    message = ed_message.text.toString(), sender_id = CommonUtils.getPrefValue(
                        context!!,
                        PrefConstants.ID
                    ).toInt()
                )
            )
            ed_message.setText("")
            rc_chat.adapter?.notifyItemInserted(list.size - 1)
        }
    }

    fun refreshChat() {
        Handler().postDelayed({
            if (!viewModel.ticketId.value.isNullOrEmpty())
                viewModel.getChat()

            refreshChat()
        }, 2000)

    }

}
