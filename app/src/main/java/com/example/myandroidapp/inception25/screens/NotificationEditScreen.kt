package com.example.myandroidapp.inception25.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.utils.NotificationHandler

@Composable
fun NotificationEditScreen(
    notificationHandler: NotificationHandler
) {
    var notificationId by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var updateResult by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = context.getString(R.string.edit_notification),
            style = MaterialTheme.typography.h5
        )

        OutlinedTextField(
            value = notificationId,
            onValueChange = { notificationId = it },
            label = { Text(context.getString(R.string.notification_id)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("1234") }
        )

        OutlinedTextField(
            value = newContent,
            onValueChange = { newContent = it },
            label = { Text(context.getString(R.string.new_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                val id = notificationId.toIntOrNull()
                if (id != null && newContent.isNotBlank()) {
                    val success = notificationHandler.updateNotification(id, newContent)
                    updateResult = if (success) {
                        context.getString(R.string.notification_updated)
                    } else {
                        context.getString(R.string.notification_not_found)
                    }
                } else {
                    updateResult = context.getString(R.string.invalid_input)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = notificationId.isNotBlank() && newContent.isNotBlank()
        ) {
            Text(context.getString(R.string.update_notification))
        }

        Button(
            onClick = {
                notificationHandler.cancelAllNotifications()
                updateResult = context.getString(R.string.all_notifications_cleared)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.error
            )
        ) {
            Text(
                text = context.getString(R.string.clear_all_notifications),
                color = MaterialTheme.colors.onError
            )
        }

        updateResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}