package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_redeem_success.*

/**
 * A simple [Fragment] subclass.
 */
class RedeemSuccess(
    var code: String,
    var name: String,
    var iRecyclerItemClick: IRecyclerItemClick
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        this.isCancelable = false

        bt_view.setOnClickListener {
            iRecyclerItemClick.onItemClick(true)
            dismiss()
        }

        if (name.isNullOrEmpty()) {
            var data =
                getNormal(getString(R.string.your_coupen_code)) + " " + getBold(code) + " " + getString(
                    R.string.was_redeemed
                )
            tv_info.text = Html.fromHtml(data)
        } else {
            var data =
                getNormal(getString(R.string.your_coupen_code)) + " " + getBold(code) + " " + getString(
                    R.string.gifted_by
                ) + " " + getBold(name) + " " + getString(R.string.was_redeemed)
            tv_info.text = Html.fromHtml(data)
        }

    }


    private fun getBold(text: String): String? {
        return "<b>$text</b>"
    }

    private fun getNormal(text: String): String? {
        return "<font>$text</font>"
    }


}
