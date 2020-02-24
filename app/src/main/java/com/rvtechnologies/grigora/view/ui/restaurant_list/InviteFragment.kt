package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.rvtechnologies.grigora.BuildConfig
import com.rvtechnologies.grigora.R

import kotlinx.android.synthetic.main.fragment_invite.*


/**
 * A simple [Fragment] subclass.
 */
class InviteFragment( var link: String) :
    DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_close.setOnClickListener { dismiss() }

        bt_invite.setOnClickListener {            dismiss()

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                link
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)

        }


    }

}
