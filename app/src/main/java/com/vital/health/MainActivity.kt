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
import androidx.hilt.navigation.compose.hiltViewModel
import com.vital.health.ui.screens.DashboardScreen
import com.vital.health.ui.screens.AuthScreen
import com.vital.health.ui.theme.VitalTheme
import com.vital.health.ui.viewmodels.HealthViewModel
import com.vital.health.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                    if (isLoggedIn) {
                        val viewModel: HealthViewModel = hiltViewModel()
                        val logs by viewModel.healthLogs.collectAsState()
                        
                        DashboardScreen(
                            logs = logs,
                            onAddLog = { type, value, unit, notes ->
                                viewModel.addLog(type, value, unit, notes)
                            },
                            onSync = {
                                viewModel.sync()
                            },
                            onLogout = {
                                authViewModel.logout()
                            }
                        )
                    } else {
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
