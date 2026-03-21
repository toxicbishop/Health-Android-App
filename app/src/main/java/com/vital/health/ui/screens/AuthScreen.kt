package com.vital.health.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.vital.health.ui.theme.CreamBg
import com.vital.health.ui.theme.PrimaryBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CreamBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VITAL",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                singleLine = true
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (isSignUpMode) onSignUp(email, password) else onLogin(email, password)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && email.isNotBlank() && password.length >= 6,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isSignUpMode) "Sign Up" else "Log In", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { isSignUpMode = !isSignUpMode }
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account? Log In" else "Don't have an account? Sign Up",
                    color = PrimaryBlack
                )
            }
        }
    }
}
