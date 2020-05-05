package com.rvtechnologies.grigora.view_model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartSharedViewModel : ViewModel() {
    var isScheduledOrder = MutableLiveData<Boolean>()
    var scheduleDate = MutableLiveData<String>()
    var scheduleTime = MutableLiveData<String>()
    var scheduleNote = MutableLiveData<String>()

    fun destroy(activity :AppCompatActivity){
        isScheduledOrder.value=null
        scheduleDate.value=null
        scheduleTime.value=null
        scheduleNote.value=null

        isScheduledOrder.removeObservers(activity)
        scheduleDate.removeObservers(activity)
        scheduleTime.removeObservers(activity)
        scheduleNote.removeObservers(activity)

    }
}