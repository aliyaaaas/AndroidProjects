package com.example.myandroidapp.inception25.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.myandroidapp.MainActivity
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.model.NotificationData
import com.example.myandroidapp.inception25.model.NotificationPriority
import com.example.myandroidapp.inception25.receiver.ReplyReceiver

class NotificationHandler(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private var areChannelsCreated = false

    fun initNotificationChannels() {
        if (areChannelsCreated) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
        areChannelsCreated = true
    }

    @SuppressLint("MissingPermission")
    fun showNotification(data: NotificationData) {
        if (!hasNotificationPermission()) {
            return
        }

        val builder = NotificationCompat.Builder(context, getChannelIdForPriority(data.priority))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(data.title)
            .setAutoCancel(true)
            .setPriority(getPriorityCompat(data.priority))

        data.content?.let { content ->
            builder.setContentText(content)

            if (data.shouldExpand && content.length > 50) {
                val bigTextStyle = NotificationCompat.BigTextStyle()
                    .bigText(content)
                    .setBigContentTitle(data.title)
                builder.setStyle(bigTextStyle)
            }
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (data.shouldOpenApp) {
                putExtra("notification_title", data.title)
                putExtra("notification_content", data.content)
            } else {
                putExtra("notification_id", data.id)
            }
        }

        val contentRequestCode = data.id * 10 + REQUEST_CODE_CONTENT
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            contentRequestCode,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(contentPendingIntent)

        if (data.hasReplyAction) {
            val replyLabel = context.getString(R.string.reply_label)
            val remoteInput = RemoteInput.Builder(ReplyReceiver.KEY_REPLY_TEXT)
                .setLabel(replyLabel)
                .build()

            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
                putExtra(ReplyReceiver.KEY_NOTIFICATION_ID, data.id)
                action = "${ReplyReceiver.ACTION_REPLY}_${data.id}"
            }

            val replyRequestCode = data.id * 10 + REQUEST_CODE_REPLY
            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                replyRequestCode,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val action = NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                replyLabel,
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        }

        try {
            notificationManager.notify(data.id, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    fun updateNotification(notificationId: Int, newContent: String): Boolean {
        if (!hasNotificationPermission()) {
            return false
        }

        val activeNotifications = notificationManager.activeNotifications
        val notificationExists = activeNotifications.any { it.id == notificationId }

        if (notificationExists && newContent.isNotBlank()) {
            val builder = NotificationCompat.Builder(context, CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.updated_notification_title))
                .setContentText(newContent)
                .setAutoCancel(true)

            try {
                notificationManager.notify(notificationId, builder.build())
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return false
    }

    fun cancelAllNotifications() {
        if (!hasNotificationPermission()) {
            return
        }

        try {
            notificationManager.cancelAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val channels = listOf(
            android.app.NotificationChannel(
                CHANNEL_MIN,
                context.getString(R.string.channel_min),
                android.app.NotificationManager.IMPORTANCE_MIN
            ),
            android.app.NotificationChannel(
                CHANNEL_LOW,
                context.getString(R.string.channel_low),
                android.app.NotificationManager.IMPORTANCE_LOW
            ),
            android.app.NotificationChannel(
                CHANNEL_DEFAULT,
                context.getString(R.string.channel_default),
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ),
            android.app.NotificationChannel(
                CHANNEL_HIGH,
                context.getString(R.string.channel_high),
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.createNotificationChannels(channels)
    }

    private fun getChannelIdForPriority(priority: NotificationPriority): String {
        return when (priority) {
            NotificationPriority.MIN -> CHANNEL_MIN
            NotificationPriority.LOW -> CHANNEL_LOW
            NotificationPriority.MEDIUM -> CHANNEL_DEFAULT
            NotificationPriority.HIGH -> CHANNEL_HIGH
        }
    }

    private fun getPriorityCompat(priority: NotificationPriority): Int {
        return when (priority) {
            NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
            NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationPriority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        }
    }

    companion object {
        const val CHANNEL_MIN = "channel_min"
        const val CHANNEL_LOW = "channel_low"
        const val CHANNEL_DEFAULT = "channel_default"
        const val CHANNEL_HIGH = "channel_high"

        private const val REQUEST_CODE_CONTENT = 0
        private const val REQUEST_CODE_REPLY = 1
    }
}