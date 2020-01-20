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
import kotlinx.android.synthetic.main.fragment_splash.*


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (CommonUtils.isDarkMode()) {
            img_logo.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.logo_text_dark
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
//        CommonUtils.savePrefs(context!!,PrefConstants.TOKEN,"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImIwNzViNTMyNzA1ZWEyMGZlMGU2OGZhMWJiMDRkYmUyZmJjMjgwODlmNTg1MGMxYTA0MDI2NDE0NDQ5M2FjMTc4MmE1YzVlYmUwYzAyY2ExIn0.eyJhdWQiOiIzIiwianRpIjoiYjA3NWI1MzI3MDVlYTIwZmUwZTY4ZmExYmIwNGRiZTJmYmMyODA4OWY1ODUwYzFhMDQwMjY0MTQ0NDkzYWMxNzgyYTVjNWViZTBjMDJjYTEiLCJpYXQiOjE1Nzk0OTY1MTUsIm5iZiI6MTU3OTQ5NjUxNSwiZXhwIjoxNjExMTE4OTE1LCJzdWIiOiIzODciLCJzY29wZXMiOltdfQ.j-c-jnDLoWphUq2nCK7d-SoGMLNTnplxmiLIDMRzNShR3cnSlPH9XfaGV3I6g3PSxRjSAOjU7TA7ywk_vMZ4trBDmRZhAo6KO_1-gX2Ul7-_egdJ3c3jcvq2NfBWfuRx26RWD44Zs0C9sw9T5W3eqx-dcfETzHbwFAINbXGakACiD6_QFeRcM-7IY2QxAmD1eej9hr2O-Mkk2yx1I1D0o_BCczWxEbHaCqCt8g9Ah3NQ7saIyOminvUg4OE0tSMQwB9u59RU3lByufs1EsfKV8r1Eu5E3zelEJ_ficTjq3TSPQ2qLemP8p5svmI0gaPQJTRTPWVWKZVAm6rmSCuhuE1Pqjna88HihRraEVwZqyuYx0hLtkXcfaQYDNFIhZflXKItJ71rIIMp8-7QF20i9uUBFU6JvUkI9QedYP8D1m0PPS6VqmVxnFIwUVcog_5A_CNEoCGGi1ZsXnTt6Sja-DLQ02519OzOOEJMuHvzbTLHV7ZlSFgmooqbR2O3twZ250M0VdU6x6lVnWi4CnZbZKik6kYGQD2oKmuVlZNLrxmnbJan8cfRx69_4WXFpe1ihBGRzs4MBJZGmxXH-zsaaTgeVbvDYB5d7cL2Go0Ch3qwsLEnKl2ewm4ZzpFZRV6UE8ZipyHG7yIEyaZB5k3-IfwnDrWZJDskrlYzPJYD45c")
//        CommonUtils.savePrefs(context!!,PrefConstants.ID,"387")
//        CommonUtils.savePrefs(context!!,PrefConstants.NAME,"Amit")
//        CommonUtils.savePrefs(context!!,PrefConstants.IMAGE,"")


        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()


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
                            ?.navigate(R.id.action_splashFragment2_to_dashboard)
                    } else
                        view?.findNavController()
                            ?.navigate(R.id.action_splashFragment2_to_selectLocationFragment2)
                } else {
                    if (CommonUtils.isFirst())
                        view?.findNavController()
                            ?.navigate(R.id.action_splashFragment2_to_welcomeFragment)
                    else
                        view?.findNavController()
                            ?.navigate(R.id.action_splashFragment2_to_chooseLanguageFragment)
                }

//                if (CommonUtils.getBooleanPrefValue(context, PrefConstants.IS_LANGUAGE_SELECTED)) {
//                    view?.findNavController()
//                        ?.navigate(R.id.action_splashFragment2_to_selectLocationFragment2)
//                } else
//                    view?.findNavController()?.navigate(R.id.action_splashFragment2_to_chooseLanguageFragment)

            }, 4000)

        }
    }


}
