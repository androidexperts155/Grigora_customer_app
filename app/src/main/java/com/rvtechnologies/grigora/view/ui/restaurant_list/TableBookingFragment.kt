package com.rvtechnologies.grigora.view.ui.restaurant_list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.TableBookingFragmentBinding
import com.rvtechnologies.grigora.model.DateModel
import com.rvtechnologies.grigora.model.TableBookingModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.DateAdapter
import com.rvtechnologies.grigora.view_model.TableBookingViewModel
import kotlinx.android.synthetic.main.table_booking_fragment.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TableBookingFragment(val args: Bundle?) : Fragment(), IRecyclerItemClick {


    private lateinit var viewModel: TableBookingViewModel
    private lateinit var currentMonthYear: Calendar
    private lateinit var tableBookingFragmentBinding: TableBookingFragmentBinding
    var list = ArrayList<DateModel>()

    var openingTime: String
    var closingTime: String
    var alwaysOpen: String
    var noOfSeates: Int
    lateinit var selectedDate: Date
    var current = 0

    init {
        noOfSeates = args?.get(AppConstants.RESTAURANT_SEATES).toString().toInt()
        openingTime = args?.get(AppConstants.RESTAURANT_OPENING_TIME).toString()
        closingTime = args?.get(AppConstants.RESTAURANT_CLOSING_TIME).toString()
        alwaysOpen = args?.get(AppConstants.RESTAURANT_ALWAYS_OPEN).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TableBookingViewModel::class.java)
        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

        viewModel.bookingDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var data = response.data as TableBookingModel
                    if (data.bookingStatus == "1") {
                        CommonUtils.showMessage(
                            parent,
                            "Request Sent. You will be notified when restaurant confirm Booking. "
                        )
                        view?.findNavController()?.popBackStack()
                    }
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tableBookingFragmentBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.table_booking_fragment,
                container,
                false
            )
        tableBookingFragmentBinding.tableBookingView = this
        tableBookingFragmentBinding.tableBookingViewModel = viewModel
        return tableBookingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        currentMonthYear = Calendar.getInstance()

        tv_month_year.text = CommonUtils.getFormattedTimeOrDate(
            currentMonthYear.time,
            "yyyy-MM-dd HH:mm:ss",
            "MMMM yyyy"
        )

// 31 - 28=3
        setCalender(
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) - CommonUtils.getFormattedTimeOrDate(
                Calendar.getInstance().time,
                "yyyy-MM-dd HH:mm:ss",
                "dd"
            ).toInt() + 1, true
        )
    }

    fun plus() {
        if (tv_count.text.toString().toInt() == noOfSeates) {
            CommonUtils.showMessage(parent, getString(R.string.ma_seates_message))
        } else
            tv_count.text = (tv_count.text.toString().toInt() + 1).toString()
    }

    fun minus() {
        if (tv_count.text.toString().toInt() > 0)
            tv_count.text = (tv_count.text.toString().toInt() - 1).toString()
    }

    fun nextMonth() {
        var temp = currentMonthYear
        temp.add(Calendar.DAY_OF_MONTH, 1)

        setCalender(temp.getActualMaximum(Calendar.DAY_OF_MONTH), false)
    }

    fun preMonth() {
        if (CommonUtils.getFormattedTimeOrDate(
                currentMonthYear.time,
                "yyyy-MM-dd HH:mm:ss",
                "MMMM yyyy"
            ) != CommonUtils.getFormattedTimeOrDate(
                Calendar.getInstance().time,
                "yyyy-MM-dd HH:mm:ss",
                "MMMM yyyy"
            )
        ) {
            var currentMonth = CommonUtils.getFormattedTimeOrDate(
                currentMonthYear.time,
                "yyyy-MM-dd HH:mm:ss",
                "MM"
            ).toInt()

            var calender = Calendar.getInstance()
            calender.set(
                CommonUtils.getFormattedTimeOrDate(
                    currentMonthYear.time,
                    "yyyy-MM-dd HH:mm:ss",
                    "YYYY"
                ).toInt(), currentMonth - 2, 1
            )
            print(calender.time.toString())
            currentMonthYear.time = calender.time
            setCalender(currentMonthYear.getActualMaximum(Calendar.DAY_OF_MONTH), false)

        }

        if (CommonUtils.getFormattedTimeOrDate(
                currentMonthYear.time,
                "yyyy-MM-dd HH:mm:ss",
                "MMMM yyyy"
            ) == CommonUtils.getFormattedTimeOrDate(
                Calendar.getInstance().time,
                "yyyy-MM-dd HH:mm:ss",
                "MMMM yyyy"
            )
        ) {
            currentMonthYear = Calendar.getInstance()
// 31 - 28=3
            setCalender(
                Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) - CommonUtils.getFormattedTimeOrDate(
                    Calendar.getInstance().time,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd"
                ).toInt() + 1, true
            )
        }


    }

    fun bookNow() {
        if (isValid()) {
            var headersMap = HashMap<String, Any>()
            headersMap["Authorization"] = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)

            var bodyMap = HashMap<String, Any?>()
            bodyMap["restaurant_id"] = args?.get(AppConstants.RESTAURANT_ID).toString()
            bodyMap["no_of_seats"] = noOfSeates.toString()
            bodyMap["date"] = CommonUtils.getFormattedTimeOrDate(
                selectedDate,
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )
            bodyMap["start_time_from"] = "${tv_hour.text.toString()}.${tv_minute.text.toString()}"
            bodyMap["start_time_to"] =
                "${tv_to_hour.text.toString()}.${tv_to_minute.text.toString()}"

            viewModel.bookTable(headersMap, bodyMap)

        }
    }

    fun hourClick() {
        current = 1

        var timeDialog = TimeBottomSheetDialog(this, getOpeningHours(), getClosingHours(), true)
        timeDialog.show(this.childFragmentManager, "")
    }

    fun minuteClick() {
        current = 2

        var timeDialog =
            TimeBottomSheetDialog(this, getOpeningMinutes(), getClosingMinutes(), false)
        timeDialog.show(this.childFragmentManager, "")
    }

    fun toHourClick() {
        current = 3


        var time = tv_hour.text.toString().toInt() + 2
        if (time == 24) {
            time = 23
        }
        var timeDialog =
            TimeBottomSheetDialog(this, time, getClosingHours() + 1, true)
        timeDialog.show(this.childFragmentManager, "")
    }

    fun toMinuteClick() {
        current = 4
        var timeDialog = TimeBottomSheetDialog(
            this,
            getOpeningMinutes(), getClosingMinutes(),
            false
        )
        timeDialog.show(this.childFragmentManager, "")
    }

    private fun setCalender(daysToLoad: Int, initial: Boolean) {
        list.clear()

        for (i in 1..daysToLoad) {
            if (!initial && i != 1)
                currentMonthYear.add(Calendar.DAY_OF_MONTH, 1)

            list.add(DateModel(currentMonthYear.time, false))
            if (i != daysToLoad && initial)
                currentMonthYear.add(Calendar.DAY_OF_MONTH, 1)
        }

        rc_dates.adapter = DateAdapter(list, this)
        tv_month_year.text = CommonUtils.getFormattedTimeOrDate(
            currentMonthYear.time,
            "yyyy-MM-dd HH:mm:ss",
            "MMMM yyyy"
        )
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            var deselectPosition = 0
            for (data in 0 until list.size) {
                if (list[data].selected) {
                    deselectPosition = data
                    list[data].selected = false
                }

                if (item == data) {
                    list[data].selected = true
                    selectedDate = list[data].date
                }
            }

            rc_dates.adapter?.notifyItemChanged(item)
            rc_dates.adapter?.notifyItemChanged(deselectPosition)
        } else {
            when (current) {
                1 -> {
                    item as String
                    tv_hour.text = item

                    tv_minute.text = "00"
                    tv_to_hour.text = "00"
                    tv_to_minute.text = "00"
                }
                2 -> {
                    item as String
                    tv_minute.text = item
                    tv_to_hour.text = "00"
                    tv_to_minute.text = "00"
                }
                3 -> {
                    item as String
                    tv_to_hour.text = item
                    tv_to_minute.text = "00"
                }
                4 -> {
                    item as String
                    tv_to_minute.text = item
                }
            }
        }

    }

    fun isCurrentMonthYear(date: Calendar): Boolean {
        return (CommonUtils.getFormattedTimeOrDate(
            date.time,
            "yyyy-MM-dd HH:mm:ss",
            "MMMM yyyy"
        ) == CommonUtils.getFormattedTimeOrDate(
            Calendar.getInstance().time,
            "yyyy-MM-dd HH:mm:ss",
            "MMMM yyyy"
        )
                )
    }

    private fun is24HourOpen(): Boolean {
        return alwaysOpen == "1"
    }

    private fun getOpeningHours(): Int {
        return if (is24HourOpen())
            0
        else CommonUtils.getFormattedTimeOrDate(
            openingTime,
            "HH:mm:ss",
            "HH"
        ).toInt()
    }

    private fun getOpeningMinutes(): Int {
        var minutes = CommonUtils.getFormattedTimeOrDate(
            openingTime,
            "HH:mm:ss",
            "mm"
        ).toInt()




        return if (is24HourOpen())
            1
        else if (minutes != 0)
            CommonUtils.getFormattedTimeOrDate(
                closingTime,
                "HH:mm:ss",
                "mm"
            ).toInt()
        else
            1
    }

    private fun getClosingHours(): Int {
        return if (is24HourOpen())
            22
        else CommonUtils.getFormattedTimeOrDate(
            closingTime,
            "HH:mm:ss",
            "HH"
        ).toInt() - 2
    }

    private fun getClosingMinutes(): Int {
        var minutes = CommonUtils.getFormattedTimeOrDate(
            closingTime,
            "HH:mm:ss",
            "mm"
        ).toInt()




        return if (is24HourOpen())
            59
        else if (minutes != 0)
            CommonUtils.getFormattedTimeOrDate(
                closingTime,
                "HH:mm:ss",
                "mm"
            ).toInt()
        else
            59
    }

    private fun isValid(): Boolean {

        if (tv_to_hour.text.toString() == "00") {
            CommonUtils.showMessage(parent, getString(R.string.error_select_closing_time))
            return false

        } else if (!::selectedDate.isInitialized) {
            CommonUtils.showMessage(parent, getString(R.string.error_select_date))
            return false
        } else if (tv_count.text.toString().equals("0")) {
            CommonUtils.showMessage(parent, getString(R.string.error_seat))
            return false
        }

        return true

    }
}




