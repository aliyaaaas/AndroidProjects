package com.example.myandroidapp.inception25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myandroidapp.inception25.data.UserDataRepository
import com.example.myandroidapp.inception25.navScreens.auth.LoginScreen
import com.example.myandroidapp.inception25.navScreens.auth.RegisterScreen
import com.example.myandroidapp.inception25.navScreens.auth.RestoreAccountScreen
import com.example.myandroidapp.inception25.navScreens.main.PlantsListScreen
import com.example.myandroidapp.inception25.navScreens.plant.AddPlantScreen
import com.example.myandroidapp.inception25.navScreens.profile.ProfileScreen
import com.example.myandroidapp.inception25.navigation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()


                NavHost(
                    navController = navController,
                    startDestination = LoginScreen
                ) {
                    composable<LoginScreen> {
                        LoginScreen(navController = navController)
                    }

                    composable<RegisterScreen> {
                        RegisterScreen(navController = navController)
                    }

                    composable<RestoreAccountScreen> { backStackEntry ->
                        val args = backStackEntry.toRoute<RestoreAccountScreen>()
                        RestoreAccountScreen(
                            navController = navController,
                            userId = args.userId,
                            userEmail = args.email
                        )
                    }

                    composable<PlantsListScreen> {
                        PlantsListScreen(navController = navController)
                    }

                    composable<AddPlantScreen> {
                        AddPlantScreen(navController = navController)
                    }

                    composable<ProfileScreen> {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}