package com.rvtechnologies.grigora.view.ui.notifications

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.NotificationsModel

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.NotificationTitleModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.NotificationsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.notifications_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment(), IRecyclerItemClick {
    var list = ArrayList<Notification>()

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

        viewModel.notificationsRes.observe(this,
            Observer { response ->
                if (response is CommonResponseModel<*>) {
                    if (response.status!!) {

                        list = ArrayList<Notification>()
                        list.addAll(response.data as Collection<NotificationsModel>)
                        var format = SimpleDateFormat("yyyy-MM-dd")

                        if (list.isNotEmpty()) {
                            var todayAdded = false
                            var yesterdayAdded = false
                            var otherdayAdded = false

                            var tempList = ArrayList<Notification>()

                            var yesterday = Calendar.getInstance()
                            yesterday.add(Calendar.DAY_OF_MONTH, -1)

                            var otherDays = Calendar.getInstance()
                            otherDays.add(Calendar.DAY_OF_MONTH, -2)

//                            to add today's notifications
                            for (data in list) {
                                data as NotificationsModel
                                if (format.parse(data.createdAt).compareTo(
                                        format.parse(
                                            format.format(
                                                Date()
                                            )
                                        )
                                    ) == 0 && !todayAdded
                                ) {
                                    tempList.add(NotificationTitleModel(getString(R.string.today)))
                                    todayAdded = true
                                }

                                if (format.parse(data.createdAt).compareTo(
                                        format.parse(
                                            format.format(
                                                yesterday.time
                                            )
                                        )
                                    ) == 0 && !yesterdayAdded
                                ) {
                                    tempList.add(NotificationTitleModel(getString(R.string.yesterday)))
                                    yesterdayAdded = true
                                }
                                if (format.parse(data.createdAt).compareTo(
                                        format.parse(
                                            format.format(
                                                yesterday.time
                                            )
                                        )
                                    ) == 0
                                ) {
                                    data.timeToShow = context!!.getString(R.string.yesterday)
                                }

                                if (format.parse(data.createdAt).compareTo(
                                        format.parse(
                                            format.format(
                                                yesterday.time
                                            )
                                        )
                                    ) != 0 && format.parse(data.createdAt).compareTo(
                                        format.parse(
                                            format.format(
                                                Date()
                                            )
                                        )
                                    ) != 0 && !otherdayAdded
                                ) {
                                    tempList.add(NotificationTitleModel(getString(R.string.earliar)))
                                    otherdayAdded = true
                                }
                                tempList.add(data)
                            }
                            list.clear()
                            list.addAll(tempList)
                            tempList.clear()

                            rv_notifcations.adapter = NotificationAdapter(list, this)
                        }
                    }
                }
            })

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notifications_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.notifications_center))
        (activity as MainActivity).img_right.setImageResource(R.drawable.ic_delete)
        (activity as MainActivity).img_right.setOnClickListener {
            viewModel.deleteNotification(
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                ""
            )
        }

        (activity as MainActivity).img_right.visibility = View.VISIBLE
        viewModel.getNotifications(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))
    }

    override fun onItemClick(item: Any) {
        if (item is NotificationsModel) {

//            Paid $amount to $otherUserName: 15
//            $userName sent you $amount.: 16
//            $amount Added to your wallet(cancel order by customer) : 8
//            $amount Added to your wallet(pickup request rejeted by customer) : 18

//            $userName sent you a gift card of $amount.: 17


//            Your Schedule order is placed waiting for restaurant confirmation : 1
//            Driver Is Reaching The Restaurant To Pick Up Your Order : 3
//            Your Order Is Picked From Restaurant : 4
//            Preparation time for order Id #$orderId has been increased due to some challenges. We apologize for the delay. : 11
//            Your Order is Almost Prepared : 7
//            Restaurant has started preparing the order : 9


//            Restaurant Not Accepted Your Booking : 18
//            Restaurant Accepted Your Booking : 19

            viewModel.deleteNotification(
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                item.id.toString()
            )

            when (item.notificationType) {

                100 -> {
                    val bundle = bundleOf(
                        AppConstants.RESTAURANT_ID to item.restautrantId,
                        AppConstants.RESTAURANT_PICKUP to item.pickup,
                        AppConstants.RESTAURANT_BOOKING to item.tableBooking,
                        AppConstants.RESTAURANT_SEATES to item.no_of_seats,
                        AppConstants.RESTAURANT_CLOSING_TIME to item.closing_time,
                        AppConstants.RESTAURANT_OPENING_TIME to item.opening_time,
                        AppConstants.RESTAURANT_ALWAYS_OPEN to item.full_time,
                        AppConstants.FROM_PICKUP to false
                    )
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_notifications_to_restaurantDetailParent,
                            bundle
                        )
                }
                1, 2, 4, 11, 7, 6, 9, 0 -> {
                    val bundle = bundleOf(
                        AppConstants.ORDER_ID to item.orderId
                    )
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_notifications_to_orderDetails,
                            bundle
                        )
                }
                15, 16, 8, 18 -> {
                    val bundle = bundleOf(
                        AppConstants.IS_FOR_HISTORY to true
                    )
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_notifications_to_transferMoney,
                            bundle
                        )
                }

                17 -> {
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_notifications_to_purchasedCards

                        )
                }

                18, 19 -> {

                }
            }


        } else {
            item as Int
            viewModel.deleteNotification(
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                (list[item] as NotificationsModel).id.toString()
            )

            list.removeAt(item)
            rv_notifcations.adapter?.notifyItemRemoved(item)

        }
    }
}
