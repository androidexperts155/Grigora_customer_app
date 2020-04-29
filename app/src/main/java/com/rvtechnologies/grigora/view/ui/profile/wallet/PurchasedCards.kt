package com.rvtechnologies.grigora.view.ui.profile.wallet

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
import com.rvtechnologies.grigora.model.MyPurchasedCardModel
import com.rvtechnologies.grigora.model.NotificationTitleModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.notifications.Notification
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.MyCardsAdapter
import com.rvtechnologies.grigora.view_model.PurchasedCardsViewModel
import kotlinx.android.synthetic.main.purchased_cards_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PurchasedCards : Fragment(), IRecyclerItemClick {
    var list = ArrayList<Notification>()

    companion object {
        fun newInstance() = PurchasedCards()
    }

    private lateinit var viewModel: PurchasedCardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.purchased_cards_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PurchasedCardsViewModel::class.java)
        viewModel.purchasedCards.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {

                    list.clear()
                    list.addAll(response.data as Collection<MyPurchasedCardModel>)
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
                            data as MyPurchasedCardModel
                            if (format.parse(data.created_at).compareTo(
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

                            if (format.parse(data.created_at).compareTo(
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


                            if (format.parse(data.created_at).compareTo(
                                    format.parse(
                                        format.format(
                                            yesterday.time
                                        )
                                    )
                                ) != 0 && format.parse(data.created_at).compareTo(
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

                        rc_cards.adapter = MyCardsAdapter(list, this)
                    }
                }
                else {
                    CommonUtils.showMessage(parent, response.message!!)
                }

            }
            else {
                CommonUtils.showMessage(parent, response.toString())
            }

        })
        viewModel.redeemRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    CommonUtils.showMessage(parent, res.message!!)
                    viewModel.getPurchasedCards()
                } else {
                    CommonUtils.showMessage(parent, res.message!!)
                }
            } else {
                CommonUtils.showMessage(parent, res.toString())
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.purchased_card))
        viewModel.getPurchasedCards()

    }

    override fun onItemClick(item: Any) {
        if (item is MyPurchasedCardModel) {
            if (item.isShare) {
                var bundle = bundleOf(AppConstants.CARD_DATA to item)
                view?.findNavController()
                    ?.navigate(R.id.action_purchasedCards_to_sarePurchasedCards, bundle)
            } else {
                viewModel.redeem(CommonUtils.getToken(), item.voucher_code)
            }
        }
    }


}
