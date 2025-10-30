package com.example.myandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myandroidapp.inception25.navScreens.AddNoteScreen
import com.example.myandroidapp.inception25.navScreens.LoginScreen
import com.example.myandroidapp.inception25.navScreens.NotesScreen
import com.example.myandroidapp.ui.theme.AppColorScheme
import com.example.myandroidapp.ui.theme.LocalAppColorScheme
import com.example.myandroidapp.ui.theme.MyAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val selectedColorScheme = remember { mutableStateOf(AppColorScheme.Purple) }

            CompositionLocalProvider(
                LocalAppColorScheme provides selectedColorScheme
            ) {
                MyAndroidAppTheme {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        composable(
                            "notes/{email}",
                            arguments = listOf(navArgument("email") { defaultValue = "" })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            NotesScreen(
                                email = email,
                                notes = emptyList(),
                                navController = navController,
                                onColorSchemeChanged = { newScheme ->
                                    selectedColorScheme.value = newScheme
                                }
                            )
                        }

                        composable(
                            "addNote/{email}",
                            arguments = listOf(navArgument("email") { defaultValue = "" })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            AddNoteScreen(
                                email = email,
                                currentNotes = emptyList(),
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}