package com.rvtechnologies.grigora.view.ui.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.SettingFragmentBinding
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.SettingViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.setting_fragment,
            container,
            false
        ) as SettingFragmentBinding
        settingFragmentBinding.settingViewModel = viewModel
        settingFragmentBinding.settingView = this
        return settingFragmentBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sw_notifications.isChecked = CommonUtils.getBooleanPrefValue(
            this.context!!,
            PrefConstants.IS_NOTIFICATIONS_ON
        )

        sw_darkmode.isChecked = CommonUtils.getBooleanPrefValue(
            this.context!!,
            PrefConstants.IS_DARK_MODE
        )

        sw_notifications.setOnClickListener {
            CommonUtils.saveBooleanPrefs(
                this.context!!,
                PrefConstants.IS_NOTIFICATIONS_ON,
                sw_notifications.isChecked
            )
        }

        sw_darkmode.setOnClickListener {
            if (sw_darkmode.isChecked) {
                switchUiMode(true)
            } else {
                switchUiMode(false)
            }
        }


    }

    private fun switchUiMode(selected: Boolean) {
        CommonUtils.saveBooleanPrefs(context!!, PrefConstants.IS_DARK_MODE, selected)
        (activity as MainActivity).recreate()
    }

    fun toChangeLanguage() {
        view?.findNavController()?.navigate(R.id.action_settingFragment_to_chooseLanguageFragment)
    }

    fun toChangePassword() {
        view?.findNavController()?.navigate(R.id.action_settingFragment_to_changePasswordFragment)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.settings))
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).showBottomNavigation(4)
        }
    }
}
