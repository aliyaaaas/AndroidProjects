package com.example.myandroidapp

import UserMessage
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myandroidapp.inception25.receiver.ReplyReceiver
import com.example.myandroidapp.inception25.screens.MessagesScreen
import com.example.myandroidapp.inception25.screens.NotificationEditScreen
import com.example.myandroidapp.inception25.screens.NotificationSettingsScreen
import com.example.myandroidapp.inception25.utils.NotificationHandler
import com.example.myandroidapp.ui.theme.MyAndroidAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var notificationHandler: NotificationHandler
    private val messages = mutableStateListOf<UserMessage>()

    private companion object {
        const val REQUEST_CODE_POST_NOTIFICATIONS = 100
        private const val EXTRA_MESSAGES = "extra_messages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationHandler = NotificationHandler(this)
        notificationHandler.initNotificationChannels()

        if (savedInstanceState != null) {
            val savedMessages = savedInstanceState.getParcelableArrayList<UserMessage>(EXTRA_MESSAGES)
            if (!savedMessages.isNullOrEmpty()) {
                messages.addAll(savedMessages)
            }
        }

        if (needsNotificationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        }

        setContent {
            MyAndroidAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                Scaffold(
                    bottomBar = {
                        AppBottomNavigation(navController = navController)
                    },
                    backgroundColor = MaterialTheme.colors.background
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Settings.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Screen.Settings.route) {
                            NotificationSettingsScreen(
                                onSendNotification = { data ->
                                    notificationHandler.showNotification(data)
                                    Toast.makeText(
                                        context,
                                        "${context.getString(R.string.notification_created)} ${data.id}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                        composable(Screen.Edit.route) {
                            NotificationEditScreen(
                                notificationHandler = notificationHandler
                            )
                        }
                        composable(Screen.Messages.route) {
                            MessagesScreen(
                                messages = messages,
                                onAddMessage = { text ->
                                    messages.add(UserMessage(text = text))
                                }
                            )
                        }
                    }
                }
            }
        }

        handleIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_MESSAGES, ArrayList(messages))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun needsNotificationPermission(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("empty_notification_click", false) == true) {
            return
        }

        when (intent?.action) {
            ReplyReceiver.ACTION_SAVE_MESSAGE -> {
                val replyText = intent.getStringExtra(ReplyReceiver.KEY_REPLY_TEXT)
                replyText?.let { text ->
                    messages.add(UserMessage(text = text, fromNotification = true))
                }
            }
            else -> {
                val title = intent?.getStringExtra("notification_title")
                val content = intent?.getStringExtra("notification_content")
                if (!title.isNullOrBlank()) {
                    val notificationText = "${getString(R.string.notification)} $title - $content"
                    messages.add(UserMessage(text = notificationText, fromNotification = true))
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(
        Screen.Settings,
        Screen.Edit,
        Screen.Messages
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    ) {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconRes),
                        contentDescription = getScreenTitle(screen)
                    )
                },
                label = { Text(getScreenTitle(screen)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun getScreenTitle(screen: Screen): String {
    val context = LocalContext.current
    return when (screen) {
        Screen.Settings -> context.getString(R.string.settings)
        Screen.Edit -> context.getString(R.string.edit)
        Screen.Messages -> context.getString(R.string.messages)
    }
}

sealed class Screen(
    val route: String,
    val titleRes: Int,
    val iconRes: Int
) {
    object Settings : Screen("settings", R.string.settings, R.drawable.ic_settings)
    object Edit : Screen("edit", R.string.edit, R.drawable.ic_edit)
    object Messages : Screen("messages", R.string.messages, R.drawable.ic_message)
}