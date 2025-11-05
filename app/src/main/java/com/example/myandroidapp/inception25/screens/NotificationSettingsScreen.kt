package com.example.myandroidapp.inception25.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.model.NotificationData
import com.example.myandroidapp.inception25.model.NotificationPriority

@Composable
fun NotificationSettingsScreen(
    onSendNotification: (NotificationData) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var shouldExpand by remember { mutableStateOf(false) }
    var shouldOpenApp by remember { mutableStateOf(false) }
    var hasReplyAction by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(NotificationPriority.MEDIUM) }
    var lastNotificationId by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current

    val titleError by derivedStateOf {
        if (title.isBlank()) context.getString(R.string.title_required) else null
    }

    val isExpandEnabled by derivedStateOf {
        content.length > 50
    }

    if (!isExpandEnabled && shouldExpand) {
        LaunchedEffect(isExpandEnabled) {
            shouldExpand = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = context.getString(R.string.notification_settings),
            style = MaterialTheme.typography.h5
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(context.getString(R.string.notification_title)) },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError != null
        )

        if (titleError != null) {
            Text(
                text = titleError!!,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(context.getString(R.string.notification_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedPriority.name,
                onValueChange = {},
                label = { Text(context.getString(R.string.notification_priority)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                            contentDescription = context.getString(R.string.select_priority)
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                NotificationPriority.entries.forEach { priority ->
                    DropdownMenuItem(onClick = {
                        selectedPriority = priority
                        expanded = false
                    }) {
                        Text(priority.name)
                    }
                }
            }
        }
        SwitchWithIcon(
            checked = shouldExpand,
            onCheckedChange = { shouldExpand = it },
            enabled = isExpandEnabled,
            iconRes = R.drawable.ic_expand,
            text = context.getString(R.string.expand_notification),
            subtitle = if (!isExpandEnabled) context.getString(R.string.expand_disabled_hint) else null
        )

        SwitchWithIcon(
            checked = shouldOpenApp,
            onCheckedChange = { shouldOpenApp = it },
            iconRes = R.drawable.ic_launch,
            text = context.getString(R.string.open_app_on_click)
        )

        SwitchWithIcon(
            checked = hasReplyAction,
            onCheckedChange = { hasReplyAction = it },
            iconRes = R.drawable.ic_reply,
            text = context.getString(R.string.add_reply_action)
        )

        lastNotificationId?.let { id ->
            LastNotificationIdCard(notificationId = id)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    val notificationData = NotificationData(
                        title = title,
                        content = content.ifBlank { null },
                        priority = selectedPriority,
                        shouldExpand = shouldExpand,
                        shouldOpenApp = shouldOpenApp,
                        hasReplyAction = hasReplyAction
                    )
                    lastNotificationId = notificationData.id
                    onSendNotification(notificationData)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = title.isNotBlank()
        ) {
            Text(context.getString(R.string.send_notification))
        }
    }
}

@Composable
private fun LastNotificationIdCard(notificationId: Int) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = context.getString(R.string.notification_id_created),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "$notificationId",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary
                )
            }
            Text(
                text = context.getString(R.string.use_this_id_for_editing),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SwitchWithIcon(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    iconRes: Int,
    text: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}