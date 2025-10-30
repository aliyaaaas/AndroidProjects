package com.example.myandroidapp.inception25.navScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myandroidapp.R

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val emailHint = stringResource(R.string.email_hint)
    val passwordHint = stringResource(R.string.password_hint)
    val hidePassword = stringResource(R.string.hide_password)
    val showPassword = stringResource(R.string.show_password)
    val loginButton = stringResource(R.string.login_button)
    val emailRequired = context.getString(R.string.email_required)
    val invalidEmail = context.getString(R.string.invalid_email)
    val passwordRequired = context.getString(R.string.password_required)
    val passwordLengthError = context.getString(R.string.password_length_error)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text(emailHint) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null
            )

            if (emailError != null) {
                Text(
                    text = emailError!!,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text(passwordHint) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) hidePassword else showPassword
                        )
                    }
                },
                isError = passwordError != null
            )

            if (passwordError != null) {
                Text(
                    text = passwordError!!,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {
                var isValid = true

                if (email.isBlank()) {
                    emailError = emailRequired
                    isValid = false
                } else if (!isValidEmail(email)) {
                    emailError = invalidEmail
                    isValid = false
                }

                if (password.isBlank()) {
                    passwordError = passwordRequired
                    isValid = false
                } else if (password.length < 8) {
                    passwordError = passwordLengthError
                    isValid = false
                }

                if (isValid) {
                    navController.navigate("notes/$email")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(loginButton)
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}