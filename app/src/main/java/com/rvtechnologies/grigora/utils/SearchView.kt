package com.rvtechnologies.grigora.utils

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.FrameLayout
import com.rvtechnologies.grigora.R
import kotlinx.android.synthetic.main.custom_search_view.view.*

class SearchView(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {
      var search_input_text: EditText

    init {
        var l = LayoutInflater.from(context)
            .inflate(R.layout.custom_search_view, this, true)

        search_input_text = l.findViewById(R.id.search_input_text)
        open_search_button.setOnClickListener { openSearch() }
        close_search_button.setOnClickListener { closeSearch() }
    }

    private fun openSearch() {
        search_input_text.setText("")
        search_open_view.visibility = View.VISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            search_open_view,
            (open_search_button.right + open_search_button.left) / 2,
            (open_search_button.top + open_search_button.bottom) / 2,
            0f, width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()

        search_input_text.requestFocus()
    }

    private fun closeSearch() {
        val circularConceal = ViewAnimationUtils.createCircularReveal(
            search_open_view,
            (open_search_button.right + open_search_button.left) / 2,
            (open_search_button.top + open_search_button.bottom) / 2,
            width.toFloat(), 0f
        )

        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                search_open_view.visibility = View.INVISIBLE
                search_input_text.setText("")
                circularConceal.removeAllListeners()
            }
        })
    }
}