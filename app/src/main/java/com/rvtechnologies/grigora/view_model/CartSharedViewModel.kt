package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartSharedViewModel : ViewModel() {
    var isScheduledOrder = MutableLiveData<Boolean>()
    var scheduleDate = MutableLiveData<String>()
    var scheduleTime = MutableLiveData<String>()
    var scheduleNote = MutableLiveData<String>()
}