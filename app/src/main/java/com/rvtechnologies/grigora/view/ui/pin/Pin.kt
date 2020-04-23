package com.rvtechnologies.grigora.view.ui.pin

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.pin_fragment.*

//addpin: DONE
//apply pin: DONE
//CHANGE PIN: PROGRESS


class Pin : Fragment() {
    var pin = ArrayList<String>()
    var numaricButtons = ArrayList<Button>()
    var imgArray = ArrayList<ImageView>()
    var previousPin = ""
    var next = 0
    var changePin = false

    companion object {
        fun newInstance() = Pin()
    }

    private lateinit var viewModel: PinViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pin_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        next = arguments?.getInt(AppConstants.NEXT)!!
        viewModel = ViewModelProviders.of(this).get(PinViewModel::class.java)

        viewModel.addPinRes.observe(this, Observer {
            CommonUtils.savePrefs(context!!, PrefConstants.PIN, listToString())
            view?.findNavController()!!.navigate(next)
        })

        viewModel.changePinRes.observe(this, Observer {
            CommonUtils.savePrefs(context!!, PrefConstants.PIN, listToString())
            view?.findNavController()!!.popBackStack()
        })

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, "Verifying")
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!CommonUtils.getPrefValue(context!!, PrefConstants.PIN).isNullOrEmpty())
            previousPin = CommonUtils.getPrefValue(context!!, PrefConstants.PIN)

        if (previousPin.isNullOrEmpty()) {
//            not have any pin, create new
            tv_title.text = getString(R.string.create_pin)
        } else if (arguments?.containsKey(AppConstants.CHANGE_PIN)!!) {
            changePin = true
            tv_title.text = getString(R.string.verify_pin)
            op1.text = ""
        } else {
//            already have pin, continue to verify
            tv_title.text = getString(R.string.verify_pin)
            op1.text = getString(R.string.forgot)
        }


        numaricButtons.add(bt1)
        numaricButtons.add(bt2)
        numaricButtons.add(bt3)
        numaricButtons.add(bt4)
        numaricButtons.add(bt5)
        numaricButtons.add(bt6)
        numaricButtons.add(bt7)
        numaricButtons.add(bt8)
        numaricButtons.add(bt9)
        numaricButtons.add(bt0)
        imgArray.add(img1)
        imgArray.add(img2)
        imgArray.add(img3)
        imgArray.add(img4)

        setClicks()
    }

    private fun setClicks() {
        op1.setOnClickListener {
            when {
                (changePin && tv_title.text.toString() == getString(R.string.verify_pin)) -> {
                    if (verifyPin()) {
//                        previous pin verified, now step 2 of change pin
                        tv_title.text = getString(R.string.create_pin)
                        op1.text = getString(R.string.setpin)
                        previousPin = listToString()
                        pin.clear()
                        updatePins()
                    }
                }
                tv_title.text.toString() == getString(R.string.create_pin) -> {
                    if (pin.size < 4) {
                        CommonUtils.showMessage(parent, getString(R.string.invalid_pin))
                    } else {
                        tv_title.text = getString(R.string.confirm_pin)
                        previousPin = listToString()
                        pin.clear()
                        updatePins()
                    }
                }
                tv_title.text.toString() == getString(R.string.confirm_pin) -> {
                    if (previousPin != listToString()) {
                        CommonUtils.showMessage(parent, getString(R.string.pin_mismatch))
                    } else {
                        //                    add pin
                        if (changePin) {
                            viewModel.changePin(
                                CommonUtils.getPrefValue(context!!, PrefConstants.PIN),
                                listToString()
                            )
                        } else
                            viewModel.addPin(listToString())
                    }
                }
                tv_title.text.toString() == getString(R.string.verify_pin) -> {
                    //                Forgot password work
                    view?.findNavController()?.navigate(R.id.action_pin_to_forgotPin)
                }
            }
        }

        op2.setOnClickListener {
            pin.removeAt(pin.size - 1)
            updatePins()
        }

        for (b in numaricButtons) {
            b.setOnClickListener {
                if (pin.size <= 3) {
                    pin.add(b.text.toString())
                    updatePins()
                }
            }
        }
    }

    private fun updatePins() {
        var selected = ContextCompat.getDrawable(context!!, R.drawable.sel_p)
        var deSelected = ContextCompat.getDrawable(context!!, R.drawable.desel)
        for (i in 0 until imgArray.size) {

            if (i < pin.size) {
                imgArray[i].setImageDrawable(selected)
            } else {
                imgArray[i].setImageDrawable(deSelected)
            }
        }

        if (pin.size > 0) {
            op2.visibility = View.VISIBLE
        } else {
            op2.visibility = View.INVISIBLE
        }
        if (pin.size == 4 && tv_title.text.toString() == getString(R.string.verify_pin))
            if (verifyPin())
                if (changePin) {
                    op1.callOnClick()
                } else
                    view?.findNavController()!!.navigate(next)
    }

    private fun listToString(): String {
        var data = ""
        for (d in pin) {
            data += d
        }
        return data
    }

    private fun verifyPin(): Boolean {
        if (previousPin == listToString()) {
            return true
        } else {
//            vibrate dots
            var selected = ContextCompat.getDrawable(context!!, R.drawable.sel_p_red)
            for (i in 0 until imgArray.size)
                imgArray[i].setImageDrawable(selected)

            YoYo.with(Techniques.Bounce)
                .duration(200)
                .playOn(li_pins)

            Handler().postDelayed({
                pin.clear()
                updatePins()
            }, 1000)
            return false
        }
    }
//    do for change pin
}
