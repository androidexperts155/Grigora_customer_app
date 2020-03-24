package com.rvtechnologies.grigora.view.ui.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.OfferFragmentBinding
import com.rvtechnologies.grigora.model.models.CartDataModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OfferModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.offers.adapter.OffersAdapter
import com.rvtechnologies.grigora.view_model.CartNdOfferViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlinx.android.synthetic.main.offer_fragment.*
import kotlinx.android.synthetic.main.offer_fragment.parentView


class OfferFragment : Fragment(), IRecyclerItemClick {

    override fun onItemClick(item: Any) {
        if (item is OfferModel) {
//            viewModel.select(item)
            viewModel.offerModel.value = item

            (activity as MainActivity).onBackPressed()
        }
    }

    companion object {
        fun newInstance() = OfferFragment()
    }

    private lateinit var viewModel: CartNdOfferViewModel

    private val offerList = ArrayList<OfferModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(CartNdOfferViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        viewModel.token.value = CommonUtils.getPrefValue(activity, PrefConstants.TOKEN)

        viewModel.offersListRes.observe(this,
            Observer { response ->
                if (response is CommonResponseModel<*>) {
                    if (response.status!! && offerList.size == 0) {
                        offerList.addAll(response.data as Collection<OfferModel>)
                        rvOffer.adapter?.notifyDataSetChanged()

                    } else {
                        CommonUtils.showMessage(parentView, response.message.toString())
                    }
                } else {
                    CommonUtils.showMessage(parentView, response.toString())
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
        val offerFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.offer_fragment,
            container,
            false
        ) as OfferFragmentBinding
        offerFragmentBinding.offerViewModel = viewModel
        return offerFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvOffer.adapter = OffersAdapter(offerList, this)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.offers))

    }
}


