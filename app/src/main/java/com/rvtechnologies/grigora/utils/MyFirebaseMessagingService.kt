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
        var destinationId = R.id.dashBoardFragment
        var args: Bundle? = null

        try {
            Log.d("FULL_NOTIFICATION_DATA", remoteMessage.data.toString())
        } catch (e: Exception) {
            Log.e("NOTIFICATION_DATA_EXC", e.message)
        }

        try {
            var notificationType =
                Gson().fromJson(remoteMessage.data.get("data"), NotificationDataModel::class.java)

// Paid $amount to $otherUserName: 15
// $userName sent you $amount.: 16
// $amount Added to your wallet(cancel order by customer) : 8
// $amount Added to your wallet(pickup request rejeted by customer) : 18
// amount is deducted from you wallet at the time of buy card : 20
// amount is deducted from you wallet at the time of buy and send gift card : 21

            if (notificationType.notificationType.toInt() == 15
                || notificationType.notificationType.toInt() == 16
                || notificationType.notificationType.toInt() == 20
                || notificationType.notificationType.toInt() == 21
                || notificationType.notificationType.toInt() == 8
                || notificationType.notificationType.toInt() == 18
            ) {
                destinationId = R.id.transferMoney
                args = bundleOf(IS_FOR_HISTORY to true)
            }

            // Restaurant Not Accepted Your Booking : 18
// Restaurant Accepted Your Booking : 22
            if (notificationType.notificationType.toInt() == 22 || notificationType.notificationType.toInt() == 19) {
                destinationId = R.id.tableBookingHistory
            }

            // $userName sent you a gift card of $amount.: 17
            if (notificationType.notificationType.toInt() == 17
            ) {
                destinationId = R.id.purchasedCards
            }

            if(notificationType.notificationType.toInt()==111){
                destinationId = R.id.quizFragment
            }

            // Your Schedule order is placed waiting for restaurant confirmation : 1
// Driver Is Reaching The Restaurant To Pick Up Your Order : 3
// Your Order Is Picked From Restaurant : 4
// Preparation time for order Id #$orderId has been increased due to some challenges. We apologize for the delay. : 11
// Your Order is Almost Prepared : 7
// Restaurant has started preparing the order : 9
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
}