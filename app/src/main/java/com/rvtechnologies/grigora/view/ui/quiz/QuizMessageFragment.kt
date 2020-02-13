package com.rvtechnologies.grigora.view.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_quiz_message.*

class QuizMessageFragment(val iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_submit.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("q")
        }
    }


}
