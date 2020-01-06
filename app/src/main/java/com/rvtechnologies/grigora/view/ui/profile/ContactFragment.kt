package com.rvtechnologies.grigora.view.ui.profile


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ContactUsModel
import com.rvtechnologies.grigora.utils.ApiConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.adapter.ConatctTypeAdapter
import com.rvtechnologies.grigorahq.network.ConnectionNetwork
import com.rvtechnologies.grigorahq.network.EventBroadcaster
 import kotlinx.android.synthetic.main.activity_main.*
 import kotlinx.android.synthetic.main.dialog_contact_us.view.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment(),EventBroadcaster {
    override fun broadcast(code: Int, data: Any?) {
        if (code == 8) {
            var pojo = Gson().fromJson(data.toString(), ContactUsModel::class.java)
            if (pojo.status) {
                showSetTimeDialog()
            } else {
                ConnectionNetwork.showSnack(
                    false,
                    activity!!,
                    parent_layout_contact,
                    getString(R.string.went_wrong)
                )
            }
        }    }
    var contact_type: ArrayList<String> = ArrayList()
    var select_type=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.temp_contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setContactype()

        tv_call.setOnClickListener {
            setCallingToCustomer(tv_phno.text.toString())
        }
        tv_phno.setOnClickListener {
            setCallingToCustomer(tv_phno.text.toString())
        }
        tv_mail.setOnClickListener {
            setMail(tv_email.text.toString())
        }
        tv_email.setOnClickListener {
            setMail(tv_email.text.toString())
        }
        btn_submit.setOnClickListener {
            if (select_type.equals("0")) {
                CommonUtils.showMessage(parentView, getString(R.string.please_select_type))


            } else {
                if (select_type.equals("complaint")) {
                    if (ed_order_id.text.isNullOrEmpty() || ed_description.text.isNullOrEmpty()) {
                        CommonUtils.showMessage(parentView, getString(R.string.required))



                    } else {
                        setSettingsApi()
                    }
                } else {
                    if (ed_description.text.isNullOrEmpty()) {
                        CommonUtils.showMessage(parentView, getString(R.string.required))

                    } else {
                        setSettingsApi()
                    }

                }
            }
        }
    }


    private fun setContactype() {
        contact_type.add(getString(R.string.select_type))
        contact_type.add(getString(R.string.complaint))
        contact_type.add(getString(R.string.feedback))
        contact_type.add(getString(R.string.Suggestions))
        contact_type.add(getString(R.string.refund))


     //   spinner.adapter = ConatctTypeAdapter(contact_type)
/*
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    if (position == 0) {
                        select_type = "0"
                    } else if (position == 1) {
                        select_type = "1"
                    } else if (position == 2) {
                        select_type = "2"
                    } else if (position == 3) {
                        select_type = "3"
                    } else if (position == 4) {
                        select_type = "4"
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }

        })*/
    }

    private fun setMail(userEmail: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:" + userEmail)
        if (intent.resolveActivity(activity!!.getPackageManager()) != null) {
            startActivity(intent)
        }
    }


    private fun setCallingToCustomer(userPhone: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + userPhone)//change the number
        startActivity(callIntent)

    }
    private fun showSetTimeDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_contact_us, null)
        val mBuilder = AlertDialog.Builder(activity!!)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.ok.setOnClickListener {
            mAlertDialog.dismiss()
activity!!.onBackPressed()
        }
    }


    private fun setSettingsApi() {
        var data = HashMap<String, Any?>()

        var headerMAp = HashMap<String, Any?>()
        headerMAp.put("Authorization", CommonUtils.getPrefValue(activity!!, PrefConstants.TOKEN))
        data.put("order_id", ed_order_id.text.toString())
        data.put("sender_type", "2")
        data.put("contact_type", select_type)
        data.put("description", ed_description.text.toString())
        ConnectionNetwork.postFormData(
            ApiConstants.CONTACT_US,
            headerMAp,
            data,
            getString(R.string.sending_request),
            activity!!,
            parent_layout_contact,
            8
        )

    }

    override fun onResume() {
        super.onResume()

        GrigoraApp.getInstance()!!.registerListener(this)


        if (activity is MainActivity) {

            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onPause() {
        super.onPause()
        GrigoraApp.getInstance()!!.deRegisterListener(this)
    }
}
