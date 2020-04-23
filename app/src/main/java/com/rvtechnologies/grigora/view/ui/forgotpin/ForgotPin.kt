package com.rvtechnologies.grigora.view.ui.forgotpin

import android.app.Notification
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.NotificationsModel

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ForgotPinFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.forgot_pin_fragment.*

class ForgotPin : Fragment() {

    companion object {
        fun newInstance() = ForgotPin()
    }

    private lateinit var viewModel: ForgotPinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ForgotPinViewModel::class.java)

        viewModel?.forgotPasswordResult?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if(response.status!!){
                    CommonUtils.showMessage(parentView, response.message!!)
                    CommonUtils.savePrefs(
                        context!!,
                        PrefConstants.PIN,
                        (response.data as NotificationsModel).pin
                    )

                    view?.findNavController()?.popBackStack()
                    view?.findNavController()?.popBackStack()
                }
                else
                    CommonUtils.showMessage(parentView, response.message!!)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
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
        val forgotPasswordBinding: ForgotPinFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.forgot_pin_fragment, container, false)

        forgotPasswordBinding.forgotPassword = this
        forgotPasswordBinding.forgotPasswordViewModel = viewModel
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
