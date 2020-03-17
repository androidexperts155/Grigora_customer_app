package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.VoucherCodeModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_show_voucher.*

class ShowVoucherFragment(
    var codeModel: VoucherCodeModel,
    var iRecyclerItemClick: IRecyclerItemClick, var isSelf: Boolean
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_voucher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        if (isSelf)
            bt_submit.text = getString(R.string.proceed_to_buy)
        else
            bt_submit.text = getString(R.string.proceed_to_gift)


        bt_submit.setOnClickListener {
            iRecyclerItemClick.onItemClick(codeModel)
            dismiss()
        }

        bt_cancel.setOnClickListener { dismiss() }
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Picasso.get()
            .load(codeModel.voucher_image).placeholder(
                circularProgressDrawable
            )
            .error(
                circularProgressDrawable
            )
            .into(img_card)
    }


}
