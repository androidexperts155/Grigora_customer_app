package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FilteredPrice
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.PriceFilterAdapter
import kotlinx.android.synthetic.main.fragment_price_dialog.*

class PriceDialog(
    val iRecyclerItemClick: IRecyclerItemClick, var filteredPrice: FilteredPrice
) : BottomSheetDialogFragment(), IRecyclerItemClick {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_price_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        rc_items.adapter = PriceFilterAdapter(filteredPrice, this)
        img_close.setOnClickListener {
            //            iRecyclerItemClick.onItemClick(SelectedRating(0F, 0F, false, filter))
            dismiss()
        }
        bt_view.setOnClickListener {

            filteredPrice.list.removeAll { !it.selected }

//            for(item in filteredPrice.list){
//                if(!item.selected)
//                    filteredPrice.list.remove(item)
//            }

            iRecyclerItemClick.onItemClick(filteredPrice)
            dismiss()
        }
    }

    override fun onItemClick(item: Any) {
        item as Int
        filteredPrice.list[item].selected = !filteredPrice.list[item].selected
        rc_items.adapter?.notifyDataSetChanged()
    }


}
