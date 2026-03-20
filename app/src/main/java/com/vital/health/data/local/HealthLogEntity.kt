package com.vital.health.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val logType: String, // BLOOD_PRESSURE, WEIGHT, HEART_RATE
    val value: String,
    val unit: String,
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
