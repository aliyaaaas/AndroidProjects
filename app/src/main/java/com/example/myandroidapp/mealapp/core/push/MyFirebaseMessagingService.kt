package com.example.myandroidapp.mealapp.core.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myandroidapp.R
import com.example.myandroidapp.mealapp.MainActivity
import com.example.myandroidapp.mealapp.core.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val kind = remoteMessage.data[Constants.PUSH_KEY_KIND]
        val title = remoteMessage.data[Constants.PUSH_KEY_TITLE].orEmpty()
        val message = remoteMessage.data[Constants.PUSH_KEY_MESSAGE].orEmpty()

        val channelId = when (kind) {
            Constants.PUSH_KIND_PROMO -> getString(R.string.notification_channel_promo_id)
            Constants.PUSH_KIND_AUTH -> getString(R.string.notification_channel_auth_id)
            else -> getString(R.string.notification_channel_general_id)
        }

        createNotificationChannel(channelId)
        showNotification(title, message, channelId)
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val (name, desc) = when (channelId) {
                getString(R.string.notification_channel_promo_id) -> getString(R.string.notification_channel_promo_name) to getString(R.string.notification_channel_promo_desc)
                getString(R.string.notification_channel_auth_id) -> getString(R.string.notification_channel_auth_name) to getString(R.string.notification_channel_auth_desc)
                else -> getString(R.string.notification_channel_general_name) to getString(R.string.notification_channel_general_desc)
            }
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelId, name, importance).apply { description = desc }.let { notificationManager.createNotificationChannel(it) }
        }
    }

    private fun showNotification(title: String, message: String, channelId: String) {
        val intent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build().let { notificationManager.notify(System.currentTimeMillis().toInt(), it) }
    }
}