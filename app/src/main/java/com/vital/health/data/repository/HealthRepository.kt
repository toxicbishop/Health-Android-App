package com.vital.health.data.repository

import com.vital.health.data.local.HealthLogDao
import com.vital.health.data.local.HealthLogEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val localDao: HealthLogDao,
    private val supabase: SupabaseClient
) {
    val allLogs: Flow<List<HealthLogEntity>> = localDao.getAllLogs()

    suspend fun addLog(type: String, value: String, unit: String, notes: String?) {
        val log = HealthLogEntity(
            logType = type,
            value = value,
            unit = unit,
            notes = notes
        )
        localDao.insertLog(log)
        // Sync logic would be called here or via WorkManager
    }

    suspend fun syncWithRemote() {
        val unsynced = localDao.getUnsyncedLogs()
        unsynced.forEach { log ->
            try {
                supabase.postgrest["health_logs"].insert(log)
                localDao.markAsSynced(log.id)
            } catch (e: Exception) {
                // Network error handled by WorkManager retries
            }
        }
    }
}
