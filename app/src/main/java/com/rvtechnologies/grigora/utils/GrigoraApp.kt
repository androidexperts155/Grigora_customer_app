package com.rvtechnologies.grigora.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import co.paystack.android.PaystackSdk
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.api.ApiClient
import com.rvtechnologies.grigorahq.network.EventBroadcaster
import io.fabric.sdk.android.Fabric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GrigoraApp : Application() {
    var listeners = ArrayList<EventBroadcaster>()

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        PaystackSdk.initialize(applicationContext)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("EXC", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token

                ApiClient.getClient().updateFirebaseToken(
                    token = CommonUtils.getPrefValue(
                        applicationContext,
                        PrefConstants.TOKEN
                    ), deviceType = "0", deviceToken = token!!, deviceId = device_id()

                )
                    .enqueue(object : Callback<JsonElement> {
                        override fun onResponse(
                            call: Call<JsonElement>?,
                            response: Response<JsonElement>?
                        ) {
                            if (response != null && response.isSuccessful)
                                Log.e("response_token", response.body().toString())
                            else {
                            }
                        }

                        override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                            Log.d("error", t!!.message)
                        }

                    })

            })

        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true) // Enables Crashlytics debugger
            .build()
        Fabric.with(fabric)
    }

    var activity: Activity? = null

    companion object {
        private var grigoraApp: GrigoraApp? = null
        fun getInstance() = grigoraApp
            ?: GrigoraApp().also {
                grigoraApp = it
            }


    }

    fun setCurrentActivity(activity: Activity) {
        this.activity = activity
    }

    fun getCurrentLanguage(): String {
        return CommonUtils.getPrefValue(activity, PrefConstants.LANGUAGE_SELECTED)
    }

    fun updateData(eventType: Int, `object`: kotlin.Any?) {
        Handler(Looper.getMainLooper()).post {
            for (listener in listeners) {
                try {
                    listener.broadcast(eventType, `object`!!)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun registerListener(listener: EventBroadcaster) {
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    fun deRegisterListener(listener: EventBroadcaster) {
        if (listeners.contains(listener))
            listeners.removeAt(listeners.indexOf(listener))
    }

    fun device_id(): String {

        Log.e(
            "device_id", Settings.Secure.getString(
                applicationContext.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
        )
        return Settings.Secure.getString(
            applicationContext.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )

    }

    fun isLogin(context: Context): Boolean{
        return  !CommonUtils.getPrefValue(context,PrefConstants.TOKEN).isNullOrEmpty()
    }


}