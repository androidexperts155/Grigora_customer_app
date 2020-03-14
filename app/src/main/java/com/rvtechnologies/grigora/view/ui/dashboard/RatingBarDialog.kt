package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SelectedRating
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_rating_bar_dialog.*


class RatingBarDialog(
    val iRecyclerItemClick: IRecyclerItemClick,
    val progress: Float,
    val filter: NewDashboardModel.Filter
) :
    BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rating_bar_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        rating_bar.setProgress(progress)
        img_close.setOnClickListener {
            iRecyclerItemClick.onItemClick(SelectedRating(0F, 0F, false, filter))
            dismiss()
        }
        bt_view.setOnClickListener {
            iRecyclerItemClick.onItemClick(
                SelectedRating(
                    progress,
                    rating_bar.progressFloat,
                    true,
                    filter
                )
            )
            dismiss()
        }
        rating_bar.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                if (seekParams.progressFloat.equals(5.0))
                    tv_over.visibility = View.GONE
                else
                    tv_over.visibility = View.VISIBLE

                tv_rating.text = seekParams.progressFloat.toString()
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                if (seekBar.progressFloat.equals(5.0))
                    tv_over.visibility = View.GONE
                else
                    tv_over.visibility = View.VISIBLE

                tv_rating.text = seekBar.progressFloat.toString()
            }
        })
    }

}
