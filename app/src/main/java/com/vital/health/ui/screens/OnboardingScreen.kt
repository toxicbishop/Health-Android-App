package com.vital.health.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital.health.ui.theme.*

@Composable
fun OnboardingScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onComplete: (email: String, password: String, name: String) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var targetBP by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val goalsPrefs = remember { context.getSharedPreferences("health_goals", Context.MODE_PRIVATE) }

    Surface(modifier = Modifier.fillMaxSize(), color = CreamBg) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Progress dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier.size(if (i == step) 12.dp else 8.dp)
                            .background(if (i <= step) PrimaryBlack else TanButton, RoundedCornerShape(50))
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            when (step) {
                0 -> {
                    Text("🏥", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Welcome to\nVital Health", color = TextMain, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 38.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Track your vitals, mood, medications,\nand stay on top of your health.", color = TextMuted, fontSize = 16.sp, textAlign = TextAlign.Center, lineHeight = 24.sp)
                }
                1 -> {
                    Text("📧", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Create Your Account", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; localError = null },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; localError = null },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(Icons.Outlined.Lock, contentDescription = if (showPassword) "Hide" else "Show", tint = TextMuted)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; localError = null },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                2 -> {
                    Text("👤", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("What's your name?", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                3 -> {
                    Text("🎯", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Set Your Goals", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You can change these later in Settings", color = TextMuted, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        label = { Text("Target Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = targetBP,
                        onValueChange = { targetBP = it },
                        label = { Text("Target BP (e.g. 120/80)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Error messages
            val displayError = localError ?: errorMessage
            if (displayError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(displayError, color = VitalError, fontSize = 14.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    when (step) {
                        0 -> step = 1
                        1 -> {
                            if (email.isBlank() || !email.contains("@")) {
                                localError = "Please enter a valid email"
                            } else if (password.length < 6) {
                                localError = "Password must be at least 6 characters"
                            } else if (password != confirmPassword) {
                                localError = "Passwords don't match"
                            } else {
                                localError = null
                                step = 2
                            }
                        }
                        2 -> if (name.isNotBlank()) step = 3
                        3 -> {
                            goalsPrefs.edit()
                                .putString("target_weight", targetWeight)
                                .putString("target_bp", targetBP)
                                .apply()
                            context.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)
                                .edit().putBoolean("onboarding_done", true).apply()
                            onComplete(email.trim(), password, name.trim())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack, contentColor = CreamBg),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = CreamBg, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        when (step) { 0 -> "Get Started"; 1 -> "Continue"; 2 -> "Continue"; else -> "Create Account" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (step > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { step--; localError = null }) {
                    Text("Back", color = TextMuted)
                }
            }
        }
    }
}
