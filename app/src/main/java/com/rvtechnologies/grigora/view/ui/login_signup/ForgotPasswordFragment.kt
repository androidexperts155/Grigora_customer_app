package com.rvtechnologies.grigora.view.ui.login_signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentForgotPasswordBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment : Fragment() {
    private var forgotPasswordViewModel: ForgotPasswordViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordViewModel =
            ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)

        forgotPasswordViewModel?.forgotPasswordResult?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                CommonUtils.showMessage(parentView, response.message!!)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        forgotPasswordViewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, "Requesting!")
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val forgotPasswordBinding: FragmentForgotPasswordBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)

        forgotPasswordBinding.forgotPassword = this
        forgotPasswordBinding.forgotPasswordViewModel = forgotPasswordViewModel
        return forgotPasswordBinding.root
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle("")
            (activity as MainActivity).lockDrawer(true)
        }
    }


}
