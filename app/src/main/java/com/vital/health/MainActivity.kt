package com.vital.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.vital.health.ui.screens.DashboardScreen
import com.vital.health.ui.screens.AuthScreen
import com.vital.health.ui.screens.OnboardingScreen
import com.vital.health.ui.theme.VitalTheme
import com.vital.health.ui.viewmodels.HealthViewModel
import com.vital.health.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("vital_prefs", android.content.Context.MODE_PRIVATE)
        com.vital.health.ui.theme.isAppDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        setContent {
            VitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                    val isLoading by authViewModel.isLoading.collectAsState()
                    val errorMessage by authViewModel.errorMessage.collectAsState()
                    var onboardingDone by remember { mutableStateOf(sharedPrefs.getBoolean("onboarding_done", false)) }

                    if (isLoggedIn) {
                        // Logged in → show dashboard
                        val viewModel: HealthViewModel = hiltViewModel()
                        val logs by viewModel.healthLogs.collectAsState()

                        DashboardScreen(
                            logs = logs,
                            userName = authViewModel.userName,
                            userEmail = authViewModel.userEmail ?: "",
                            userAvatarUrl = authViewModel.userAvatarUrl,
                            onSaveProfile = { newName, photoBytes ->
                                authViewModel.updateProfile(newName, photoBytes)
                            },
                            onAddLog = { type, value, unit, notes ->
                                viewModel.addLog(type, value, unit, notes)
                            },
                            onSync = {
                                viewModel.sync()
                            },
                            onBackup = {
                                viewModel.backup()
                            },
                            onRestore = {
                                viewModel.restore()
                            },
                            onLogout = {
                                authViewModel.logout()
                            }
                        )
                    } else if (!onboardingDone) {
                        // First time → onboarding with signup
                        OnboardingScreen(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onComplete = { email, password, name ->
                                authViewModel.signUp(email, password)
                                authViewModel.updateProfile(name, null)
                                onboardingDone = true
                            }
                        )
                    } else {
                        // Returning user → login screen
                        AuthScreen(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onLogin = { email, pass -> authViewModel.login(email, pass) },
                            onSignUp = { email, pass -> authViewModel.signUp(email, pass) }
                        )
                    }
                }
            }
        }
    }
}
