package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.UserDetails
import com.rvtechnologies.grigora.utils.CommonUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileDetailsViewModel : ViewModel() {

    val token = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val image = MutableLiveData<String>()


    val isLoading = MutableLiveData<Boolean>()
    val userDetailsRes = MutableLiveData<Any>()


    fun getProfileData() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getProfileData(
                token = token.value.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<UserDetails>>() {}.type
                    userDetailsRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    userDetailsRes.value = result
                }
            }
    }

    fun saveProfile() {
        isLoading.value = true
        val name = RequestBody.create(MediaType.parse("multipart/form"), name.value.toString())
        val phone = RequestBody.create(MediaType.parse("multipart/form"), phone.value.toString())

        val profilePic: MultipartBody.Part?
        if (!CommonUtils.isLink(image.value.toString())) {
            val imgFile = File(CommonUtils.compressFile(image.value.toString()))
            val profileImage_ = RequestBody.create(MediaType.parse("image/*"), imgFile)
            profilePic = MultipartBody.Part.createFormData("image", imgFile.name, profileImage_)
        } else {
            val profileImage_ = RequestBody.create(MediaType.parse("image/*"), "")
            profilePic = MultipartBody.Part.createFormData("", "", profileImage_)
        }

        ApiRepo.getInstance()
            .saveProfile(
                token = token.value.toString(),
                name = name,
                phone = phone,
                image = profilePic
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<UserDetails>>() {}.type
                    userDetailsRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    userDetailsRes.value = result
                }
            }
    }
}
