package com.vital.health.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HealthLogEntity::class], version = 1, exportSchema = false)
abstract class VitalDatabase : RoomDatabase() {
    abstract fun healthLogDao(): HealthLogDao
}
