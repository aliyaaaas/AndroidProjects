package com.example.myandroidapp.inception25.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.example.myandroidapp.MainActivity

class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        Thread {
            try {
                if (intent.action?.startsWith(ACTION_REPLY) == true) {
                    val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, -1)
                    val replyText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_REPLY_TEXT)?.toString()

                    if (!replyText.isNullOrBlank() && notificationId != -1) {
                        val saveIntent = Intent(context, MainActivity::class.java).apply {
                            action = ACTION_SAVE_MESSAGE
                            putExtra(KEY_REPLY_TEXT, replyText)
                            putExtra(KEY_NOTIFICATION_ID, notificationId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        context.startActivity(saveIntent)
                    }

                    if (notificationId != -1) {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                        notificationManager.cancel(notificationId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }.start()
    }

    companion object {
        const val KEY_REPLY_TEXT = "key_reply_text"
        const val KEY_NOTIFICATION_ID = "key_notification_id"
        const val ACTION_SAVE_MESSAGE = "action_save_message"
        const val ACTION_REPLY = "action_reply"
    }
}