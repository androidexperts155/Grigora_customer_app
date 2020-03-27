package com.rvtechnologies.grigora.view.ui.restaurant_list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.GroupOrderFragmentBinding
import com.rvtechnologies.grigora.model.CreateGroupOrderModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.GroupOrderViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.group_order_fragment.*
import kotlinx.android.synthetic.main.refer_and_earn_fragment.*
import java.util.*

class GroupOrderFragment(val args: Bundle?, val iRecyclerItemClick: IRecyclerItemClick) :
    Fragment(), IRecyclerItemClick {

    private lateinit var groupOrderFragmentBinding: GroupOrderFragmentBinding
    var selected_amount = "0"

    private lateinit var viewModel: GroupOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GroupOrderViewModel::class.java)

        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })


        viewModel.createGroupOrderRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    var data = res.data as CreateGroupOrderModel
                    generateLink(data)


                }
            }
        })
        viewModel.updateCartLink.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    var data = res.data as CreateGroupOrderModel
                    iRecyclerItemClick.onItemClick(data.id.toString())
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupOrderFragmentBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.group_order_fragment,
                container,
                false
            )
        groupOrderFragmentBinding.orderView = this
        return groupOrderFragmentBinding.root

    }

    fun none() {
        selected_amount = "50"
        tv_none.setBackgroundResource(R.drawable.none_sel)
        tv_none.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)


    }

    fun p1() {
        selected_amount = "100"
        tv_p1.setBackgroundResource(R.drawable.price_sel)
        tv_p1.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_none.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p2() {
        selected_amount = "200"

        tv_p2.setBackgroundResource(R.drawable.price_sel)
        tv_p2.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_none.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p3() {
        selected_amount = "500"

        tv_p3.setBackgroundResource(R.drawable.price_sel)
        tv_p3.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_none.setTextColor(color)
        tv_p4.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun p4() {
        selected_amount = "1000"

        tv_p4.setBackgroundResource(R.drawable.price_sel)
        tv_p4.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_none.setTextColor(color)


        tv_others.setBackgroundResource(R.drawable.others_de_sel)
        tv_others.setTextColor(color)
    }

    fun moreInfo() {
        var groupOrderInfoDialog = GroupOrderInfoDialog()
        groupOrderInfoDialog.show(this.childFragmentManager, "")
    }

    fun other() {

        tv_others.setBackgroundResource(R.drawable.others_sel)
        tv_others.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

        tv_p1.setBackgroundResource(R.drawable.price_de_sel)
        tv_p2.setBackgroundResource(R.drawable.price_de_sel)
        tv_p3.setBackgroundResource(R.drawable.price_de_sel)
        tv_p4.setBackgroundResource(R.drawable.price_de_sel)
        tv_none.setBackgroundResource(R.drawable.none_de_sel)

        var color = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        } else {
            ContextCompat.getColor(context!!, R.color.text_hint_color)
        }

        tv_none.setTextColor(color)
        tv_p1.setTextColor(color)
        tv_p2.setTextColor(color)
        tv_p3.setTextColor(color)
        tv_p4.setTextColor(color)


        var otherAmount = OtherAmount(this)
        otherAmount.show(this.childFragmentManager, "")
    }

    fun startOrder() {
        viewModel.createGroupOrder(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
            selected_amount,
            args?.get(AppConstants.RESTAURANT_ID).toString()
        )
    }

    private fun generateLink(data: CreateGroupOrderModel) {
        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("content/12345")
            .setTitle("Group Order")
            .setContentDescription(
                "${CommonUtils.getPrefValue(
                    context!!,
                    PrefConstants.NAME
                )} invited you to order from ${data.restaurant_name}"
            )
            .setContentImageUrl("http://3.13.78.53/GriGora/public/images/grigora.png")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(
                ContentMetadata().addCustomMetadata(
                    AppConstants.CART_ID,
                    data.id.toString()
                ).addCustomMetadata(AppConstants.RESTAURANT_ID, data.restaurant_id.toString())
            )

        val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")
            .addControlParameter("custom_random", Calendar.getInstance().timeInMillis.toString())

        buo.generateShortUrl(
            context!!, lp
        ) { url, error ->
            if (error == null) {
                viewModel.saveCartLink(
                    token = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                    link = url, cartId = data.id.toString()
                )
            }
        }

//        val ss =
//            ShareSheetStyle(activity as MainActivity, "Check this out!", "This stuff is awesome: ")
//                .setCopyUrlStyle(
//                    ContextCompat.getDrawable(
//                        context!!,
//                        android.R.drawable.ic_menu_send
//                    ), "Copy", "Added to clipboard"
//                )
//                .setMoreOptionStyle(
//                    ContextCompat.getDrawable(
//                        context!!,
//                        android.R.drawable.ic_menu_search
//                    ), "Show more"
//                )
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.WHATS_APP)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FLICKR)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.PINTEREST)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
//                .setAsFullWidthStyle(true)
//                .setSharingTitle("Share With")
//
//        buo.showShareSheet(
//            activity as MainActivity,
//            lp,
//            ss,
//            object : Branch.BranchLinkShareListener {
//                override fun onShareLinkDialogLaunched() {}
//                override fun onShareLinkDialogDismissed() {}
//                override fun onLinkShareResponse(
//                    sharedLink: String?,
//                    sharedChannel: String?,
//                    error: BranchError?
//                ) {
//                }
//
//                override fun onChannelSelected(channelName: String) {}
//            })
    }

    override fun onItemClick(item: Any) {
        if (item is String) {
            selected_amount = item

            tv_others.text = "₦".plus(item)
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).fab_cart.visibility=View.GONE
    }
}
