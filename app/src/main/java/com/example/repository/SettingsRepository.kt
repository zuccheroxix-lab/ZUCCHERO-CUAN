package com.example.repository

import com.example.data.SettingsDataStore
import com.example.data.UserSettings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStore: SettingsDataStore) {

    val settings: Flow<UserSettings> = dataStore.settingsFlow

    suspend fun setMonitorInterval(seconds: Int) {
        dataStore.setMonitorInterval(seconds)
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        dataStore.setAnimationsEnabled(enabled)
    }

    suspend fun setAutoRefresh(enabled: Boolean) {
        dataStore.setAutoRefresh(enabled)
    }

    suspend fun setThemeAccent(accent: String) {
        dataStore.setThemeAccent(accent)
    }

    suspend fun recordOptimized() {
        dataStore.setLastOptimized(System.currentTimeMillis())
    }

    suspend fun resetAll() {
        dataStore.resetAll()
    }
}
