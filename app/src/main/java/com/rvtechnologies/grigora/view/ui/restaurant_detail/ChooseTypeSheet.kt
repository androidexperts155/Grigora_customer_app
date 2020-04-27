package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.SheetTypeAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SheetTypeModel
import kotlinx.android.synthetic.main.fragment_choose_type_sheet.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseTypeSheet(
    var selection: String,
    var restaurantDetailModel: RestaurantDetailNewModel,
    var list: ArrayList<RestaurantDetailNewModel.AllData>,
    val iRecyclerItemClick: IRecyclerItemClick
) : BottomSheetDialogFragment(), IRecyclerItemClick {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_type_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        rc_items.adapter = SheetTypeAdapter(list, this)

        bt_cancel.setOnClickListener { dismiss() }
        tv_name.text = restaurantDetailModel.restaurant_name
        tv_selected.text = selection


        if (restaurantDetailModel.full_time == "1") {
            tv_time.text = getString(R.string.open_24_hours)
        } else
            tv_time.text = CommonUtils.getFormattedUtc(
                restaurantDetailModel.opening_time,
                "HH:mm:ss",
                "hh:mm aa"
            ) + " to " + CommonUtils.getFormattedUtc(
                restaurantDetailModel.closing_time,
                "HH:mm:ss",
                "hh:mm aa"
            )
    }

    override fun onItemClick(item: Any) {
        iRecyclerItemClick.onItemClick(item)
        dismiss()
    }

}
