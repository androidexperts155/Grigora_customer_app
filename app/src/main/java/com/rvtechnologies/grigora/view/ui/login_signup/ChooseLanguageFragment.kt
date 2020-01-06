package com.rvtechnologies.grigora.view.ui.login_signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentChooseLanguageBinding
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ChooseLanguageViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_choose_language.*

class ChooseLanguageFragment : Fragment() {
    private var chooseLanguageViewModel: ChooseLanguageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseLanguageViewModel =
            ViewModelProviders.of(this).get(ChooseLanguageViewModel::class.java)

        chooseLanguageViewModel?.langSelectionResult?.observe(this, Observer { response ->
            if (response is String) {
                CommonUtils.saveBooleanPrefs(context, PrefConstants.IS_LANGUAGE_SELECTED, true)

                CommonUtils.savePrefs(context, PrefConstants.LANGUAGE_SELECTED, response)
                GrigoraApp.getInstance().setCurrentActivity(activity as MainActivity)
                (activity as MainActivity).updateLocale(true)

                view?.findNavController()
                    ?.navigate(R.id.action_chooseLanguageFragment_to_selectLocationFragment)
            }
        })

        chooseLanguageViewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, getString(R.string.setting_up))
            } else {
                CommonUtils.hideLoader()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chooseLanguageBinding: FragmentChooseLanguageBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_choose_language, container, false)
        chooseLanguageBinding.chooseLanguageViewModel = chooseLanguageViewModel
        chooseLanguageBinding.chooseLanguage = this

        return chooseLanguageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        var language = CommonUtils.getPrefValue(context, PrefConstants.LANGUAGE_SELECTED)
        if (language == AppConstants.FRENCH) {
            rel_french.setBackgroundResource(R.drawable.select_lang_round_grey)
            tv_french.setTextColor(ContextCompat.getColor(context!!, R.color.white))

            rel_english.setBackgroundResource(R.drawable.select_lang_round_white)
            tv_english.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

        } else {
            rel_english.setBackgroundResource(R.drawable.select_lang_round_grey)
            tv_english.setTextColor(ContextCompat.getColor(context!!, R.color.white))

            rel_french.setBackgroundResource(R.drawable.select_lang_round_white)
            tv_french.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).deliverLayout.visibility = View.GONE
        (activity as MainActivity).img_menu.visibility = View.GONE
        (activity as MainActivity).img_back.visibility = View.VISIBLE
        (activity as MainActivity).lockDrawer(true)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChooseLanguageFragment()
    }

    fun setEnglish() {
        rel_english.setBackgroundResource(R.drawable.select_lang_round_grey)
        tv_english.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        rel_french.setBackgroundResource(R.drawable.select_lang_round_white)
        tv_french.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        chooseLanguageViewModel?.langSelected?.value = AppConstants.ENGLISH
    }

    fun setFrench() {
        rel_english.setBackgroundResource(R.drawable.select_lang_round_white)
        tv_english.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        rel_french.setBackgroundResource(R.drawable.select_lang_round_grey)
        tv_french.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        chooseLanguageViewModel?.langSelected?.value = AppConstants.FRENCH
    }
}
