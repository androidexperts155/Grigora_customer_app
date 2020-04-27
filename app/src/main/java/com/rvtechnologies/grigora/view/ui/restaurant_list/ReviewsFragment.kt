package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ReviewsFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.ReviewItem
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantReviewAdapter
import kotlinx.android.synthetic.main.reviews_fragment.*

class ReviewsFragment : Fragment() {

    companion object {
        fun newInstance() = ReviewsFragment()
    }

    private lateinit var viewModel: ReviewsViewModel
    private val reviewItemList = ArrayList<ReviewItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val reviewsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.reviews_fragment,
            container,
            false
        ) as ReviewsFragmentBinding
        return reviewsFragmentBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReviewsViewModel::class.java)
        if (arguments?.getString(AppConstants.RESTAURANT_ID) != null) {
            val resId = arguments?.getString(AppConstants.RESTAURANT_ID)
            viewModel.id.value = resId
            viewModel.reviewListRes.observe(this, Observer { reviewRes ->
                if (reviewRes is CommonResponseModel<*>) {
                    if (reviewRes.status!!) {
                        reviewItemList.clear()
                        reviewItemList.addAll(reviewRes.data as Collection<ReviewItem>)
                        rvReviews.adapter = RestaurantReviewAdapter(reviewItemList)
                    } else {
                        CommonUtils.showMessage(parentView, reviewRes.message!!)
                    }
                } else
                    CommonUtils.showMessage(parentView, reviewRes.toString())
            })
        }
        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.reviews))
        (activity as MainActivity).lockDrawer(true)
        viewModel.getRestaurantsReviews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
