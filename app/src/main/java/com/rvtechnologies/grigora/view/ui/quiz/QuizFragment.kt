package com.rvtechnologies.grigora.view.ui.quiz

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.QuizModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.QuizViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.quiz_fragment.*

class QuizFragment : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = QuizFragment()
    }

    private lateinit var viewModel: QuizViewModel
    lateinit var quizModel: QuizModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.quiz_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                activity?.let {
                    com.rvtechnologies.grigora.utils.CommonUtils.showLoader(
                        it,
                        getString(R.string.loading)
                    )
                }
            } else {
                CommonUtils.hideLoader()
            }
        })
        viewModel.quizQuestionResponse.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    quizModel = res.data as QuizModel
                    rc_answers.adapter = QuizAdapter(quizModel, this)
                    tv_question.text = quizModel.question

                    if (!quizModel.image.isNullOrEmpty()) {
                        val circularProgressDrawable = CircularProgressDrawable(context!!)
                        circularProgressDrawable.strokeWidth = 5f
                        circularProgressDrawable.centerRadius = 30f
                        circularProgressDrawable.start()
                        Picasso.get()
                            .load(quizModel.image).placeholder(
                                circularProgressDrawable
                            )
                            .error(
                                circularProgressDrawable
                            )
                            .into(img_data)
                    }
                }
            }
        })

        viewModel.submitQuizResponse.observe(this, Observer { res ->
            var restaurantRatingDialogFragment = QuizMessageFragment(this)
            restaurantRatingDialogFragment.isCancelable = false
            restaurantRatingDialogFragment.show(childFragmentManager, "")

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getQuizQuestion(
            CommonUtils.getPrefValue(
                context!!,
                PrefConstants.TOKEN
            )
        )

        bt_submit.setOnClickListener {

            if (quizModel.selected != -1) {
                viewModel.submitAnswer(
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                    quizModel.question,
                    quizModel.options[quizModel.selected]
                )
            } else
                CommonUtils.showMessage(parent, getString(R.string.please_select_an_answer))
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.quiz_of_the_day))
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            quizModel.selected = item
            rc_answers.adapter!!.notifyDataSetChanged()
        } else { Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment).popBackStack()
            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .navigate(R.id.dashBoardFragment)
        }
    }

}
