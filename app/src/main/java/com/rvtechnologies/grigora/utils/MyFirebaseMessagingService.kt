package com.rvtechnologies.grigora.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.NotificationDataModel
import com.rvtechnologies.grigora.utils.AppConstants.IS_FOR_HISTORY
import com.rvtechnologies.grigora.utils.AppConstants.MESSAGE
import com.rvtechnologies.grigora.utils.AppConstants.NOTIFICATION_TYPE
import com.rvtechnologies.grigora.utils.AppConstants.ORDER_ID
import com.rvtechnologies.grigora.utils.AppConstants.PREPARING_TIME_RESTAURANT
import com.rvtechnologies.grigora.utils.AppConstants.TYPE
import com.rvtechnologies.grigora.view.ui.MainActivity

import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        val NOTIFICATION_CHANNEL_ID = "10001"
    }


    override fun onNewToken(p0: String) {
        Log.d(TAG, "onNewToken: " + p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage?.data != null) {
            showNotificationMessage(remoteMessage)
        }
    }

    internal fun showNotificationMessage(remoteMessage: RemoteMessage) {
        var destinationId = 0
        var args: Bundle? = null


        Log.d("notification_data", remoteMessage.data.get("data"))
        Log.d("sendby", remoteMessage.data.get("sendby"))
        Log.d("body", remoteMessage.data.get("body"))

        try {
            var notificationType =
                Gson().fromJson(remoteMessage.data.get("data"), NotificationDataModel::class.java)

//        0=>Waiting for confirmation
//        2=>accepted by restaurant,
//        3=>driver assigned,
//        9=>restaurant starts preparing  get time from notification
//        7=> order is almost ready,   stop prepation time/nill
//        4=>out of delivery,  get driver time
//        5=> deliverd,   stop driver time
//        1=> schedule order ,
//        6=>rejected by restaurant,
//        8=>cancelled by customer,
//        (notification type )11=> show elapsed time and reset counter

            if (notificationType.notificationType.toInt() == 15 || notificationType.notificationType.toInt() == 16 || notificationType.notificationType.toInt() == 8
                || notificationType.notificationType.toInt() == 18
            ) {
                destinationId = R.id.transferMoney
                args = bundleOf(IS_FOR_HISTORY to true)
            }

            if (notificationType.notificationType.toInt() == 17
            ) {
                destinationId = R.id.purchasedCards

            }

            if (
                notificationType.notificationType.toInt() == 0 ||
                notificationType.notificationType.toInt() == 1 ||
                notificationType.notificationType.toInt() == 2 ||
                notificationType.notificationType.toInt() == 9 ||
                notificationType.notificationType.toInt() == 8 ||
                notificationType.notificationType.toInt() == 3 ||
                notificationType.notificationType.toInt() == 6 ||
                notificationType.notificationType.toInt() == 4 ||
                notificationType.notificationType.toInt() == 5 ||
                notificationType.notificationType.toInt() == 11 ||
                notificationType.notificationType.toInt() == 7
            ) {
                destinationId = R.id.orderDetailsFragment
                args = bundleOf(ORDER_ID to notificationType.orderId)

                val intent = Intent()
                intent.action = "com.rvtechnologies.grigora"
                intent.putExtra(NOTIFICATION_TYPE, notificationType.notificationType)
                intent.putExtra(ORDER_ID, notificationType.orderId.toString())
                intent.putExtra(TYPE, notificationType.type)

                intent.putExtra(MESSAGE, remoteMessage.data["body"]!!)
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

                if (notificationType.notificationType.toInt() == 11)
                    intent.putExtra(PREPARING_TIME_RESTAURANT, notificationType.preparing_time)
                else if (notificationType.notificationType.toInt() == 9)
                    intent.putExtra(
                        PREPARING_TIME_RESTAURANT,
                        notificationType.restaurant_preparing_time
                    )
                sendBroadcast(intent)

                if (notificationType.notificationType.toInt() == 6) {
                    notificationType.message = remoteMessage.data["body"]!!
                }
            }

            if (notificationType.notificationType.toInt() == 112) {
                val intent = Intent()
                intent.action = "com.rvtechnologies.grigora"
                intent.putExtra(NOTIFICATION_TYPE, notificationType.notificationType)
                intent.putExtra(AppConstants.TICKET_ID, notificationType.orderId)
                intent.putExtra(TYPE, notificationType.type)

                intent.putExtra(MESSAGE, remoteMessage.data["body"]!!)
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                sendBroadcast(intent)

                if (notificationType.notificationType.toInt() == 6) {
                    notificationType.message = remoteMessage.data["body"]!!
                }
            }


        } catch (e: Exception) {
            print(e.message)
        }

        if (CommonUtils.getBooleanPrefValue(this.baseContext, PrefConstants.IS_NOTIFICATIONS_ON)) {
            var largeIcon: Bitmap? = null
            if (resources != null)
                largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logo)

            val icon = R.drawable.logo
            val name: String
            val message: String?

            val resultIntent: Intent

            name = remoteMessage.data.get("sendby")!!
            message = remoteMessage.data.get("body")
            resultIntent = Intent(applicationContext, MainActivity::class.java)

            resultIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP


            val pendingIntent: PendingIntent
            pendingIntent = if (args != null) {
                NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(destinationId)
                    .setArguments(args)
                    .createPendingIntent()
            } else
                NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(destinationId)
                    .createPendingIntent()

            val mBuilder = NotificationCompat.Builder(applicationContext)
            val inboxStyle = NotificationCompat.InboxStyle()

            inboxStyle.addLine(message)
            val notificationCompat: NotificationCompat.Builder?
            if (largeIcon != null)

                notificationCompat = mBuilder
                    .setSmallIcon(icon)
                    .setLargeIcon(largeIcon)
                    .setTicker(applicationContext.getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentTitle(name)
                    .setContentIntent(pendingIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(inboxStyle)
                    .setContentText(message)
            else
                notificationCompat = mBuilder
                    .setSmallIcon(icon)
                    .setTicker(applicationContext.getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentTitle(name)
                    .setContentIntent(pendingIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(inboxStyle)
                    .setContentText(message)

            val notification = notificationCompat.build()

            val mNotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.setShowBadge(true)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                mNotificationManager.createNotificationChannel(notificationChannel)
                mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
            } else {
                mNotificationManager.notify(System.currentTimeMillis().toInt(), notification)
            }
        }
    }

    private fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + applicationContext.packageName + "/raw/notification"
            )
            val r = RingtoneManager.getRingtone(applicationContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showNoti() {
        val notiId = Date().time.toInt()
        var notiChannel = "1"
        val intent = Intent(this, MainActivity::class.java)
        notiChannel = "4"
        intent.putExtra("from", "notification")
        intent.putExtra("type", "1")
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

        val builder: NotificationCompat.Builder
        builder = NotificationCompat.Builder(applicationContext, notiChannel)
        builder.setAutoCancel(true)

        builder.setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(R.string.Quiz))
            .setSound(null)
            .setContentText(getString(R.string.new_quiz_available)).setContentIntent(pendingIntent)

        builder.setStyle(NotificationCompat.BigTextStyle())
        val mNotificationManager =
            this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "131",
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED

            notificationChannel.setShowBadge(true)

            assert(mNotificationManager != null)
            builder.setChannelId("131")
            mNotificationManager.createNotificationChannel(notificationChannel)
            mNotificationManager.notify(notiId, builder.build())
        } else
            mNotificationManager.notify(notiId, builder.build())
    }
}

/*

when rest accept order
{
    "restaurant_image":"http:\/\/3.13.78.53\/GriGora\/public\/images\/brands\/1579176534.png",
    "notification_type":"2",
    "restaurant_address":"Mohali Railway Station Rd, Phase 11, Sector 65, Sahibzada Ajit Singh Nagar, Punjab, India",
    "restaurant_lat":"30.679366",
    "order_id":"196",
    "restaurant_preparing_time":"3",
    "restaurant_name":"Subway",
    "restaurant_long":"76.7267531"
}*/

/*
when restaurant add more time to meal
{
    "notification_type":"11",
    "start_long":"76.7267531",
    "max_per_person":null,
    "driver_id":null,
    "delivery_address":"127, Mohali Bypass, Phase 8, Industrial Area, Sector 73, Sahibzada Ajit Singh Nagar, Punjab 160071, India,160071,India",
    "end_lat":"30.7130455",
    "dispatch":"0",
    "restaurant_id":369,
    "created_at":"2020-03-16 10:54:52",
    "delivery_time":null,
    "cancel_type":null,
    "price_before_promo":"121.00",
    "reference":"",
    "cart_id":253,
    "delivery_fee":"20.00",
    "order_status":"2",
    "request_time":"2020-03-16T10:58:59.764767Z",
    "updated_at":"2020-03-16 10:58:59",
    "final_price":"141.00",
    "start_lat":"30.679366",
    "app_fee":10,
    "is_schedule":"0",
    "payment_data":"",
    "id":196,
    "price_after_promo":"121.00",
    "order_accepted_time":null,
    "order_type":"1",
    "payment_method":"3",
    "preparing_end_time":"2020-03-16 10:58:34",
    "driver_fee":"20.00",
    "notification_sent":"0",
    "quantity":1,
    "preparing_time":"5",
    "promocode":"",
    "delivery_note":null,
    "time_remaining":null,
    "schedule_time":null,
    "user_id":387,
    "end_long":"76.709415",
    "start_address":"Mohali Railway Station Rd, Phase 11, Sector 65, Sahibzada Ajit Singh Nagar, Punjab, India",
    "group_order":"0"
}*/


/*
when order is ready to dispatch

{
    "restaurant_image":"http:\/\/3.13.78.53\/GriGora\/public\/images\/brands\/1579176534.png",
    "notification_type":"7",
    "restaurant_address":"Mohali Railway Station Rd, Phase 11, Sector 65, Sahibzada Ajit Singh Nagar, Punjab, India",
    "restaurant_lat":"30.679366",
    "order_id":196,
    "restaurant_name":"Subway",
    "restaurant_long":"76.7267531"
}*/



