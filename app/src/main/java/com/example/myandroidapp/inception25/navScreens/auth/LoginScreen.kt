package com.example.myandroidapp.inception25.navScreens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.data.UserDataRepository
import com.example.myandroidapp.inception25.di.ServiceLocator
import com.example.myandroidapp.inception25.navigation.*
import com.example.myandroidapp.inception25.utils.ValidationUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val userRepository = ServiceLocator.getUserRepository()
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.login_email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errorMessage != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.login_password)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage != null
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val emailError = ValidationUtils.validateEmail(email, context)
                        val passwordError = ValidationUtils.validatePassword(password, context)

                        when {
                            emailError != null -> {
                                errorMessage = emailError
                                return@Button
                            }
                            passwordError != null -> {
                                errorMessage = passwordError
                                return@Button
                            }
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch {
                            val (isDeleted, deletedUserId) = userRepository.checkIfUserDeleted(email)

                            if (isDeleted && deletedUserId != null) {
                                isLoading = false
                                navController.navigate(RestoreAccountScreen(deletedUserId, email))
                                return@launch
                            }

                            val userId = userRepository.loginUser(email, password)
                            isLoading = false

                            if (userId != null) {
                                UserDataRepository.saveCurrentUserId(userId)
                                navController.navigate(PlantsListScreen)
                            } else {
                                errorMessage = context.getString(R.string.login_invalid_credentials)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    navController.navigate(RegisterScreen)
                }
            ) {
                Text(stringResource(R.string.login_no_account))
            }
        }
    }
}