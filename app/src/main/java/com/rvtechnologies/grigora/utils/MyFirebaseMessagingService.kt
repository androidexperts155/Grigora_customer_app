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
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.NotificationDataModel
import com.rvtechnologies.grigora.utils.AppConstants.MESSAGE
import com.rvtechnologies.grigora.utils.AppConstants.NOTIFICATION_TYPE
import com.rvtechnologies.grigora.utils.AppConstants.ORDER_ID
import com.rvtechnologies.grigora.utils.AppConstants.TYPE
import com.rvtechnologies.grigora.view.ui.MainActivity
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        val NOTIFICATION_CHANNEL_ID = "10001"
    }

//    override fun onNewToken(s: String?) {
//        super.onNewToken(s)
//        Log.d(TAG, "onNewToken: " + s!!)
//    }

    override fun onNewToken(p0: String) {
        Log.d(TAG, "onNewToken: " + p0)
    }

//    Bundle[
//    {
//        google.delivered_priority =
//            high, content-available = 1, google.sent_time = 1567422817731, google.ttl = 2419200, google.original_priority = high, sendby = Grigora, body = Order Is Accepted By Driver, data ={ "order_id":3 }, from = 652242170156, type = Grigora, badge = 0, sound = default, google.message_id = 0:1567422817737898%65d2fb8bf9fd7ecd, establishment_detail = Grigora
//    }]

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage?.data != null) {
            showNotificationMessage(remoteMessage)
//            GrigoraApp.getInstance()!!.updateData(
//                BroadcastConstants.noti_new_order,
//                remoteMessage.data.get("body")
//            )
//            MyApplication.instance!!.updateData(
//                BroadcastConstants.noti_ongoing,
//                remoteMessage.data.get("body")
//            )
//            MyApplication.instance!!.updateData(
//                BroadcastConstants.noti_past,
//                remoteMessage.data.get("body")
//            )
        }
    }


    internal fun showNotificationMessage(remoteMessage: RemoteMessage) {
        try {
            var notificationType =
                Gson().fromJson(remoteMessage.data.get("data"), NotificationDataModel::class.java)

            if (
                notificationType.notificationType.toInt() == 0 ||
                notificationType.notificationType.toInt() == 2 ||
                notificationType.notificationType.toInt() == 8 ||
                notificationType.notificationType.toInt() == 3 ||
                notificationType.notificationType.toInt() == 6 ||
                notificationType.notificationType.toInt() == 4 ||
                notificationType.notificationType.toInt() == 5 ||
                notificationType.notificationType.toInt() == 7
            ) {
                val intent = Intent()
                intent.action = "com.rvtechnologies.grigora"
                intent.putExtra(NOTIFICATION_TYPE, notificationType.notificationType)
                intent.putExtra(ORDER_ID, notificationType.orderId)
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
            val resultPendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

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
                    .setContentIntent(resultPendingIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(inboxStyle)
                    .setContentText(message)
            else
                notificationCompat = mBuilder
                    .setSmallIcon(icon)
                    .setTicker(applicationContext.getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentTitle(name)
                    .setContentIntent(resultPendingIntent)
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