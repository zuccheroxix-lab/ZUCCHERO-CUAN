package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zx_settings")

data class UserSettings(
    val monitorIntervalSeconds: Int = 3,
    val animationsEnabled: Boolean = true,
    val autoRefreshEnabled: Boolean = true,
    val themeAccent: String = "CYAN",
    val lastOptimizedTimestamp: Long = 0L
)

class SettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val MONITOR_INTERVAL = intPreferencesKey("monitor_interval_sec")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        val THEME_ACCENT = stringPreferencesKey("theme_accent")
        val LAST_OPTIMIZED = longPreferencesKey("last_optimized")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            monitorIntervalSeconds = preferences[PreferencesKeys.MONITOR_INTERVAL] ?: 3,
            animationsEnabled = preferences[PreferencesKeys.ANIMATIONS_ENABLED] ?: true,
            autoRefreshEnabled = preferences[PreferencesKeys.AUTO_REFRESH] ?: true,
            themeAccent = preferences[PreferencesKeys.THEME_ACCENT] ?: "CYAN",
            lastOptimizedTimestamp = preferences[PreferencesKeys.LAST_OPTIMIZED] ?: 0L
        )
    }

    suspend fun setMonitorInterval(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MONITOR_INTERVAL] = seconds
        }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun setAutoRefresh(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH] = enabled
        }
    }

    suspend fun setThemeAccent(accent: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_ACCENT] = accent
        }
    }

    suspend fun setLastOptimized(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_OPTIMIZED] = timestamp
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
