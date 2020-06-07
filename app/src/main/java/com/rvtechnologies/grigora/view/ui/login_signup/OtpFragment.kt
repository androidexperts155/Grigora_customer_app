package com.rvtechnologies.grigora.view.ui.login_signup

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
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.otp_fragment.*


class OtpFragment : Fragment() {

    companion object {
        fun newInstance() = OtpFragment()
    }

    private lateinit var viewModel: OtpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.otp_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OtpViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                activity?.let {
                    CommonUtils.showLoader(
                        it,
                        getString(R.string.loading)
                    )
                }
            } else {
                CommonUtils.hideLoader()
            }
        })
        viewModel.resendOtpRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    CommonUtils.showMessage(parentView, res.message!!)
                } else {
                    CommonUtils.showMessage(parentView, res.message!!)
                }
            } else {
                CommonUtils.showMessage(parentView, res.toString())
            }
        })

        viewModel.verifyOtpRes.observe(this, Observer { response ->
            if (response is LoginResponseModel) {
                if (response.status!!) {
                    if (arguments?.getInt(AppConstants.FROM) == 1) {
//                        from signup
                        CommonUtils.showMessage(parentView, getString(R.string.verify_email))
                        view?.findNavController()?.popBackStack()
                        view?.findNavController()?.popBackStack()
                    } else if (arguments?.getInt(AppConstants.FROM) == 2 || arguments?.getInt(
                            AppConstants.FROM
                        ) == 3
                    ) {
                        if (response.email_verified!!)
                            saveData(response)
                        else if (!response.email_verified!!) {
                            CommonUtils.showMessage(parentView, getString(R.string.verify_email))
                            view?.findNavController()?.popBackStack()
                            view?.findNavController()?.popBackStack()
                        }
//                        fromm login
                    } else if (arguments?.getInt(AppConstants.FROM) == 3) {
//                        fromm social login
                    }

                } else {

                    CommonUtils.showMessage(parentView, response.message!!)
                }
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        tv_sent_to.text =
            getString(R.string.otp_sent_to) + arguments!!.getString(AppConstants.PHONE).toString()

        buttonVerifyPhone.setOnClickListener {
            val code = otp_view.text.toString()
            if (code.length < 6) {
                otp_view.error = getString(R.string.invalid_otp)

            } else {
                viewModel.verifyOtp(arguments?.getString(AppConstants.USER_ID)!!, code)
            }
        }

        buttonResend.setOnClickListener {
            viewModel.resend(arguments?.getString(AppConstants.USER_ID)!!)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle("")
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())
        CommonUtils.savePrefs(context, PrefConstants.PIN, data.data?.pin?.toString())
        CommonUtils.savePrefs(context, PrefConstants.EMAIL, data.data?.email?.toString())


        if (data?.data?.have_address!!) {
            view?.findNavController()?.navigate(R.id.action_otpFragment_to_addressListFragment)
        } else {
            view?.findNavController()
                ?.navigate(R.id.action_otpFragment_to_selectLocationFragment)
        }
    }
}
