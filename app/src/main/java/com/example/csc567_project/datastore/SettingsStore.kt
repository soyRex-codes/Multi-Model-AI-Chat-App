package com.example.csc567_project.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {
    suspend fun setAPIUrl(url: String) {
        val key = stringPreferencesKey("settings_apiUrl")
        context.settingsStore.edit { prefs ->
            prefs[key] = url
        }
    }

    fun getAPIUrl(): Flow<String> {
        val key = stringPreferencesKey("settings_apiUrl")
        return context.settingsStore.data.map { prefs ->
            prefs[key] ?: ""
        }
    }

    suspend fun setAPIProvider(provider: String) {
        val key = stringPreferencesKey("settings_apiProvider")
        context.settingsStore.edit { prefs ->
            prefs[key] = provider
        }
    }

    fun getAPIProvider(): Flow<String> {
        val key = stringPreferencesKey("settings_apiProvider")
        return context.settingsStore.data.map { prefs ->
            prefs[key] ?: ""
        }
    }
}