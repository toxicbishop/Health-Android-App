package com.vital.health.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val supabase: SupabaseClient
) {
    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    fun currentUserId(): String? {
        return supabase.auth.currentSessionOrNull()?.user?.id
    }

    suspend fun login(userEmail: String, userPass: String) {
        supabase.auth.signInWith(Email) {
            email = userEmail
            password = userPass
        }
    }

    suspend fun signUp(userEmail: String, userPass: String) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPass
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }
}
