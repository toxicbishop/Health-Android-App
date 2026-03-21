package com.vital.health.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.*
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

    fun currentUserEmail(): String? {
        return supabase.auth.currentSessionOrNull()?.user?.email
    }
    
    fun currentUserName(): String? {
        val json = supabase.auth.currentSessionOrNull()?.user?.userMetadata
        return json?.get("full_name")?.jsonPrimitive?.content
    }

    fun currentUserAvatar(): String? {
        val json = supabase.auth.currentSessionOrNull()?.user?.userMetadata
        return json?.get("avatar_url")?.jsonPrimitive?.content
    }

    suspend fun updateProfile(name: String, avatarUrl: String?) {
        supabase.auth.updateUser {
            data = buildJsonObject {
                put("full_name", name)
                if (avatarUrl != null) {
                    put("avatar_url", avatarUrl)
                }
            }
        }
    }
    
    suspend fun uploadAvatar(bytes: ByteArray, fileName: String): String {
        supabase.storage["avatars"].upload(fileName, bytes) {
            upsert = true
        }
        return supabase.storage["avatars"].publicUrl(fileName)
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
