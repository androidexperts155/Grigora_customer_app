package com.rvtechnologies.grigora.view.ui.restaurant_detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailFragmentBinding
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.FeaturedAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ParentsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.MealsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.FeaturedModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SheetTypeModel
import com.rvtechnologies.grigora.view_model.RestaurantDetailViewModel
import kotlinx.android.synthetic.main.restaurant_detail_fragment.*

class RestaurantDetail : Fragment(), IRecyclerItemClick {
    private var EXPANDED = "expanded"
    private var COLLAPESD = "collapsed"

    companion object {
        fun newInstance() = RestaurantDetail()
    }

    private lateinit var viewModel: RestaurantDetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val restaurantDetailFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.restaurant_detail_fragment,
            container,
            false
        ) as RestaurantDetailFragmentBinding
        restaurantDetailFragmentBinding.restaurantDetailFragment = this
        return restaurantDetailFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFeaturedAdapter()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantDetailViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
    }

    private fun setFeaturedAdapter() {

        var list = ArrayList<FeaturedModel>()
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        list.add(FeaturedModel())
        rec_featured.adapter = FeaturedAdapter(list, this)
        var list1 = ArrayList<FeaturedModel>()
        list1.add(FeaturedModel())
        list1.add(FeaturedModel())
        list1.add(FeaturedModel())
        rec_previously.adapter = MealsAdapter(list1, this)
        rec_parents.adapter = ParentsAdapter(list1, this)
    }

    fun pickupClicked() {

    }

    fun deliveryClicked() {

    }

    fun chooseType() {
        var list = ArrayList<SheetTypeModel>()
        list.add(SheetTypeModel("Breakfast"))
        list.add(SheetTypeModel("Lunch"))
        list.add(SheetTypeModel("Dinner"))

        var sheet = ChooseTypeSheet(list, this)
        sheet.show(childFragmentManager, "")
    }


    override fun onItemClick(item: Any) {
        if (item is SheetTypeModel) {
            bt_type.text = item.name
        } else if (item is FeaturedModel) {
            var sheet = MealDetailSheet()
            sheet.show(childFragmentManager, "")
        } else
            view?.findNavController()?.navigate(R.id.action_restaurantDetail_to_mealsList)
    }

}
