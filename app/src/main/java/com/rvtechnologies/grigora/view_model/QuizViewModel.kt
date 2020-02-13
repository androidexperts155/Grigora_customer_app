package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.QuizModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CartDataModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class QuizViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var quizQuestionResponse: MutableLiveData<Any> = MutableLiveData()
    var submitQuizResponse: MutableLiveData<Any> = MutableLiveData()

    fun getQuizQuestion(token: String) {
        isLoading.value = true

        ApiRepo.getInstance().getQuizQuestion(
            token = token
        ) { success, result ->
            isLoading.value = false
            if (success) {
                val type = object : TypeToken<CommonResponseModel<QuizModel>>() {}.type

                quizQuestionResponse.value = Gson().fromJson(
                    result as JsonElement, type
                )
            } else {
                quizQuestionResponse.value = result
            }
        }
    }

    fun submitAnswer(token: String, question: String, answer: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .submitAnswer(
                token = token, question_id = question, answer = answer
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<QuizModel>>() {}.type

                    submitQuizResponse.value = Gson().fromJson(
                        result as JsonElement, type
                    )
                } else {
                    submitQuizResponse.value = result
                }
            }
    }
}
