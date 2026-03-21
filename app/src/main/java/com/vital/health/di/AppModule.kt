package com.vital.health.di

import android.content.Context
import androidx.room.*
import com.vital.health.data.local.HealthLogDao
import com.vital.health.data.local.VitalDatabase
import com.vital.health.data.remote.AuthManager
import com.vital.health.data.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabase(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://ffporhmbjhthtaiaunyq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZmcG9yaG1iamh0aHRhaWF1bnlxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQwMTc0NzMsImV4cCI6MjA4OTU5MzQ3M30.BJ1yqC3csM0Bl57fqI8qB48PU_x8oBwn3n3AHnUQngw"
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VitalDatabase {
        return Room.databaseBuilder(
            context,
            VitalDatabase::class.java,
            "vital_db"
        ).build()
    }

    @Provides
    fun provideHealthDao(db: VitalDatabase): HealthLogDao = db.healthLogDao()

    @Provides
    @Singleton
    fun provideAuthManager(supabase: SupabaseClient): AuthManager = AuthManager(supabase)

    @Provides
    @Singleton
    fun provideRepository(dao: HealthLogDao, supabase: SupabaseClient): HealthRepository = HealthRepository(dao, supabase)
}
