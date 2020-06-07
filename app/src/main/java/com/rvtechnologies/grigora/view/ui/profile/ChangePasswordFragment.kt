package com.rvtechnologies.grigora.view.ui.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ChangePasswordFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ChangePasswordViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.change_password_fragment.*

class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val changePasswordBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.change_password_fragment,
            container,
            false
        ) as ChangePasswordFragmentBinding
        changePasswordBinding.changePasswordModel = viewModel
        return changePasswordBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel.changePasswordRes.observe(this, Observer { changePasswordRes ->
            if (changePasswordRes is CommonResponseModel<*>) {
                changePasswordRes.message?.let { CommonUtils.showMessage(parentView, it) }
            } else {
                CommonUtils.showMessage(parentView, changePasswordRes.toString())
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonVerifyPhone.setOnClickListener { changePassword() }
    }

    fun changePassword() {
        var valid = true

        if (!CommonUtils.isValidPassword(etNewPassword.text.toString())) {
            valid = false
            etNewPassword.error = getString(R.string.invalid_password)
        }

        if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
            etConfirmPassword.error = getString(R.string.invalid_confirm_password)
            valid = false
        }


        if (valid)
            viewModel.changePassword();
    }


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }
}
