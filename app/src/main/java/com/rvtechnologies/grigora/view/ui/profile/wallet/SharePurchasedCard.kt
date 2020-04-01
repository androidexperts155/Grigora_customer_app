package com.rvtechnologies.grigora.view.ui.profile.wallet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.MyPurchasedCardModel
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.UsersAdapter
import com.rvtechnologies.grigora.view_model.SharePurchasedCardViewModel
import kotlinx.android.synthetic.main.share_purchased_card_fragment.*

class SharePurchasedCard : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = SharePurchasedCard()
    }

    lateinit var data: MyPurchasedCardModel

    private lateinit var viewModel: SharePurchasedCardViewModel

    var users = ArrayList<SearchUserModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SharePurchasedCardViewModel::class.java)
        data = arguments?.get(AppConstants.CARD_DATA) as MyPurchasedCardModel

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.searchResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    users.clear()
                    users.addAll(response.data as ArrayList<SearchUserModel>)
                    rc_users.adapter = UsersAdapter(users, this)
                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if (response != null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })


        viewModel.sendGift.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    CommonUtils.showMessage(parent, response?.message!!)
                    view?.findNavController()?.popBackStack()
                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if (response != null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.share_purchased_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_search.setOnClickListener {
            viewModel?.getUsers(
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                ed_search.text.toString()
            )
            CommonUtils.hideKeyboard(activity as MainActivity)
        }
    }

    override fun onItemClick(item: Any) {
        if (item is SearchUserModel) {
//            viewModel.sendGift()
            viewModel.sendGift(item.email, data.voucher_code)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.grigora_gift_card))
    }

}
