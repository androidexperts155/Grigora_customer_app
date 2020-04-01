package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.AllCartModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_previous_cart.*

/**
 * A simple [Fragment] subclass.
 */
class PreviousCart(var iRecyclerItemClick: IRecyclerItemClick, var allCartModel: AllCartModel) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous_cart, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)




        tv_res1.text = allCartModel.login_cart.restaurant_name
        tv_itm1.text = allCartModel.login_cart.quantity.toString() + " " + getString(R.string.items)

        tv_res2.text = allCartModel.logout_cart.restaurant_name
        tv_itm2.text =
            allCartModel.logout_cart.quantity.toString() + " " + getString(R.string.items)


        rel_pre.setOnClickListener {
            rd_pre.isChecked = true
            rd_recent.isChecked = false
            allCartModel.seleted = allCartModel.login_cart.id.toString()


            if (CommonUtils.isDarkMode()) {
                rel_pre.setBackgroundColor(ContextCompat.getColor(context!!, R.color.viewbg_dark))
                rel_recent.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.transparent
                    )
                )

            } else {
                rel_pre.setBackgroundColor(ContextCompat.getColor(context!!, R.color.viewbg_light))
                rel_recent.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.transparent
                    )
                )
            }
        }
        rel_recent.setOnClickListener {
            allCartModel.seleted = allCartModel.logout_cart.id.toString()
            rd_pre.isChecked = false
            rd_recent.isChecked = true


            if (CommonUtils.isDarkMode()) {
                rel_recent.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.viewbg_dark
                    )
                )
                rel_pre.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))

            } else {
                rel_recent.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.viewbg_light
                    )
                )
                rel_pre.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
            }
        }

        bt_view.setOnClickListener {
            if (allCartModel.seleted.isNullOrEmpty()) {
                CommonUtils.showMessage(parent, getString(R.string.no_cart_selected))
            } else {
                iRecyclerItemClick.onItemClick(allCartModel)
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var displayMetrics = DisplayMetrics()
        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = (displayMetrics.widthPixels - (displayMetrics.widthPixels / 9))

        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = width
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams

        this.isCancelable = false
    }

}
