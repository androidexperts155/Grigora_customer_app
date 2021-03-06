package com.rvtechnologies.grigora.view.ui.cart

import android.app.AlertDialog
import android.app.TimePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ScheduleOrderFragmentBinding
import com.rvtechnologies.grigora.model.DayModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.DayAdapter
import com.rvtechnologies.grigora.view_model.CartSharedViewModel
import kotlinx.android.synthetic.main.schedule_order_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleOrder : Fragment(), IRecyclerItemClick {
    var daysList = ArrayList<DayModel>()
    private var fromRestaurant = false
    lateinit var openingTime: String
    lateinit var closingTime: String
    lateinit var alwaysOpen: String

    private lateinit var sharedViewModel: CartSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel =
            activity!!.let { ViewModelProviders.of(it).get(CartSharedViewModel::class.java) }
        fromRestaurant = arguments?.get(AppConstants.FROM_RESTAURANT_DETAIL) as Boolean
        openingTime = arguments?.get(AppConstants.RESTAURANT_OPENING_TIME).toString()
        closingTime = arguments?.get(AppConstants.RESTAURANT_CLOSING_TIME).toString()
        alwaysOpen = arguments?.get(AppConstants.RESTAURANT_ALWAYS_OPEN).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = DataBindingUtil.inflate(
            inflater,
            R.layout.schedule_order_fragment,
            container,
            false
        ) as ScheduleOrderFragmentBinding
        binding.scheduleView = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDates()
        if (fromRestaurant)
            bt_done.text = getString(R.string.continueTxt)

        tv_address.text = CommonUtils.getPrefValue(context!!, PrefConstants.ADDRESS)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.schedule_your_order))

    }

    private fun setDates() {
        var date = Calendar.getInstance()



        daysList.add(
            DayModel(
                getString(R.string.today),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd"),
                CommonUtils.getFormattedTimeOrDate(
                    date.time,
                    "yyy-MM-dd HH:mm:ss",
                    "yyy-MM-dd"
                ),
                true
            )
        )
        date.add(Calendar.DAY_OF_MONTH, 1)

        daysList.add(
            DayModel(
                getString(R.string.tomorrow),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd"),
                CommonUtils.getFormattedTimeOrDate(
                    date.time,
                    "yyy-MM-dd HH:mm:ss",
                    "yyy-MM-dd"
                )
            )
        )

        date.add(Calendar.DAY_OF_MONTH, 1)
        daysList.add(
            DayModel(
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "EEE"),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd"),
                CommonUtils.getFormattedTimeOrDate(
                    date.time,
                    "yyy-MM-dd HH:mm:ss",
                    "yyy-MM-dd"
                )
            )
        )

        date.add(Calendar.DAY_OF_MONTH, 1)
        daysList.add(
            DayModel(
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "EEE"),
                CommonUtils.getFormattedTimeOrDate(date.time, "yyy-MM-dd HH:mm:ss", "dd"),
                CommonUtils.getFormattedTimeOrDate(
                    date.time,
                    "yyy-MM-dd HH:mm:ss",
                    "yyy-MM-dd"
                )
            )
        )
        sharedViewModel.scheduleDate.value = daysList[0].fullDate

        rc_days.adapter = DayAdapter(daysList, this)

    }

    override fun onItemClick(item: Any) {
        if (item is String) {
            var deSelected = 0
            for (i in 0.until(daysList.size)) {
                if (daysList[i].selected && i != item.toInt()) {
                    deSelected = i
                }
            }
            daysList[item.toInt()].selected = true
            sharedViewModel.scheduleDate.value = daysList[item.toInt()].fullDate

            rc_days.adapter?.notifyItemChanged(item.toInt())
            daysList[deSelected].selected = false
            rc_days.adapter?.notifyItemChanged(deSelected)
        }
    }

    fun breakfast() {
        tv_hours.text = "07"
        tv_minutes.text = "00"
        tv_am_pm.text = "AM"

        sharedViewModel.scheduleTime.value =
            "07" + ":" + "00" + ":00"

        tv_breakfast.setBackgroundResource(R.drawable.food_time_selected)
        tv_lunch.setBackgroundResource(R.drawable.food_time_de_selected)
        tv_dinner.setBackgroundResource(R.drawable.food_time_de_selected)

        tv_breakfast.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_breakfast_selected),
            null,
            null
        )
        tv_lunch.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_lunch_de_selected),
            null,
            null
        )
        tv_dinner.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_dinner_de_selected),
            null,
            null
        )

        if (CommonUtils.isDarkMode()) {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.white))

            tv_breakfast_time.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorPrimaryDark
                )
            )
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            tv_breakfast_time.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorPrimaryDark
                )
            )
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

        }

    }

    fun lunch() {
        tv_hours.text = "12"
        tv_minutes.text = "00"
        tv_am_pm.text = "PM"

        sharedViewModel.scheduleTime.value =
            "12" + ":" + "00" + ":00"


        tv_breakfast.setBackgroundResource(R.drawable.food_time_de_selected)
        tv_lunch.setBackgroundResource(R.drawable.food_time_selected)
        tv_dinner.setBackgroundResource(R.drawable.food_time_de_selected)

        tv_breakfast.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_breakfast_de_selected),
            null,
            null
        )
        tv_lunch.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_lunch_selected),
            null,
            null
        )
        tv_dinner.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_dinner_de_selected),
            null,
            null
        )


        if (CommonUtils.isDarkMode()) {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.white))

            tv_breakfast_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            tv_breakfast_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        }

    }

    fun dinner() {
        tv_hours.text = "08"
        tv_minutes.text = "00"
        tv_am_pm.text = "PM"

        sharedViewModel.scheduleTime.value =
            "20" + ":" + "00" + ":00"

        tv_breakfast.setBackgroundResource(R.drawable.food_time_de_selected)
        tv_lunch.setBackgroundResource(R.drawable.food_time_de_selected)
        tv_dinner.setBackgroundResource(R.drawable.food_time_selected)

        tv_breakfast.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_breakfast_de_selected),
            null,
            null
        )
        tv_lunch.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_lunch_de_selected),
            null,
            null
        )
        tv_dinner.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(context!!, R.drawable.ic_dinner_selected),
            null,
            null
        )
        if (CommonUtils.isDarkMode()) {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

            tv_breakfast_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        } else {
            tv_breakfast.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_lunch.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_dinner.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

            tv_breakfast_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_lunch_time.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_dinner_time.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        }
    }

    fun chooseTime() {
        var timePickerDialog = TimePickerDialog(
            context!!, R.style.TimePickerTheme,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                if (hourOfDay > 12) {
                    var h = (hourOfDay - 12)
                    if (h < 10)
                        tv_hours.text = "0$h"
                    else
                        tv_hours.text = "$h"
                } else {
                    var h = hourOfDay
                    if (h < 10)
                        tv_hours.text = "0$h"
                    else
                        tv_hours.text = "$h"
                }

                tv_minutes.text = minute.toString()

                if (hourOfDay >= 12) {
                    tv_am_pm.text = "PM"
                } else {
                    tv_am_pm.text = "AM"
                }

                sharedViewModel.scheduleTime.value =
                    hourOfDay.toString() + ":" + tv_minutes.text.toString() + ":00"
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1,
            Calendar.getInstance().get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    fun changeAddress() {
        val builder = AlertDialog.Builder(activity!!, R.style.TimePickerTheme)
        //set title for alert dialog
        builder.setTitle(R.string.change_title)
        //set message for alert dialog
        builder.setMessage(R.string.change_desc)
        //performing positive action
        builder.setPositiveButton(R.string.continueTxt) { dialogInterface, which ->
            dialogInterface.dismiss()
            CommonUtils.savePrefs(context, PrefConstants.LATITUDE, "")
            CommonUtils.savePrefs(context, PrefConstants.LONGITUDE, "")
            CommonUtils.savePrefs(context, PrefConstants.ADDRESS, "")

            view?.findNavController()
                ?.navigate(
                    R.id.action_scheduleOrder_to_selectLocationFragment
                )
        }

        //performing negative action
        builder.setNegativeButton(R.string.places_cancel) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun schedule() {
        if (!ed_note.text.toString().isNullOrBlank())
            sharedViewModel.scheduleNote.value = ed_note.text.toString()
        else
            sharedViewModel.scheduleNote.value = ""

        sharedViewModel.isScheduledOrder.value = true







        when {

            sharedViewModel.scheduleTime.value.isNullOrEmpty() -> {
                CommonUtils.showMessage(parentView, getString(R.string.please_select_time))
            }
            sharedViewModel.scheduleDate.value.isNullOrEmpty() -> {
                CommonUtils.showMessage(parentView, getString(R.string.please_select_date))
            }
            fromRestaurant -> {
                view?.findNavController()?.navigate(R.id.action_scheduleOrder_to_navigationCart)
            }
            else -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                var scheduleDate =
                    sdf.parse("${sharedViewModel.scheduleDate.value} ${sharedViewModel.scheduleTime.value}")
                if (scheduleDate.compareTo(Date()) < 1) {
                    CommonUtils.showMessage(parentView, getString(R.string.past_time))
                } else
                    view?.findNavController()?.popBackStack()
            }
        }
    }
}
