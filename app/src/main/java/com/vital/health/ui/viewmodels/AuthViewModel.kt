package com.vital.health.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital.health.data.remote.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    // Automatically picks up cached session perfectly when the app launches!
    val isLoggedIn: StateFlow<Boolean> = authManager.sessionStatus
        .map { it is SessionStatus.Authenticated }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authManager.currentUserId() != null
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val userEmail: String?
        get() = authManager.currentUserEmail()
        
    var userName by mutableStateOf(authManager.currentUserName() ?: (userEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User"))
        private set
        
    var userAvatarUrl by mutableStateOf(authManager.currentUserAvatar())
        private set

    fun updateProfile(name: String, photoBytes: ByteArray?) {
        viewModelScope.launch {
            try {
                var newAvatarUrl = userAvatarUrl
                if (photoBytes != null) {
                    val fileName = "${authManager.currentUserId()}_avatar.jpg"
                    newAvatarUrl = authManager.uploadAvatar(photoBytes, fileName)
                }
                authManager.updateProfile(name, newAvatarUrl)
                userName = name
                userAvatarUrl = newAvatarUrl
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authManager.login(email, pass)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authManager.signUp(email, pass)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Sign up failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authManager.logout()
            } catch (e: Exception) {
                _errorMessage.value = "Logout failed"
            }
        }
    }
}
