package com.example.myandroidapp.inception25.screens

import UserMessage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    messages: List<UserMessage>,
    onAddMessage: (String) -> Unit
) {
    var newMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = context.getString(R.string.messages),
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.no_messages),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.sortedBy { it.timestamp }) { message ->
                    MessageItem(message = message)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                label = { Text(context.getString(R.string.type_message)) },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )

            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        onAddMessage(newMessage)
                        newMessage = ""
                    }
                },
                enabled = newMessage.isNotBlank()
            ) {
                Text(context.getString(R.string.add))
            }
        }
    }
}

@Composable
private fun MessageItem(message: UserMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = if (message.fromNotification) {
            MaterialTheme.colors.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colors.surface
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (message.fromNotification) Icons.Default.Notifications else Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}