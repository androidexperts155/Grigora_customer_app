package com.rvtechnologies.grigora.view.ui.login_signup


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).updateLocale(false)

            Handler().postDelayed({
                if (GrigoraApp.getInstance().isLogin(this.context!!)) {
                    var latitude = 0.0
                    if (!CommonUtils.getPrefValue(
                            context,
                            PrefConstants.LATITUDE
                        ).isNullOrEmpty()
                    )
                        latitude =
                            CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble()

                    if (latitude > 0.0

                    ) {
                        view?.findNavController()
                            ?.navigate(R.id.action_splashFragment2_to_navigationRestaurants)
                    } else
                        view?.findNavController()
                            ?.navigate(R.id.action_splashFragment2_to_selectLocationFragment2)
                } else {
                    view?.findNavController()
                        ?.navigate(R.id.action_splashFragment2_to_welcomeFragment)
                }

//                if (CommonUtils.getBooleanPrefValue(context, PrefConstants.IS_LANGUAGE_SELECTED)) {
//                    view?.findNavController()
//                        ?.navigate(R.id.action_splashFragment2_to_selectLocationFragment2)
//                } else
//                    view?.findNavController()?.navigate(R.id.action_splashFragment2_to_chooseLanguageFragment)

            }, 2000)

        }
    }


}
