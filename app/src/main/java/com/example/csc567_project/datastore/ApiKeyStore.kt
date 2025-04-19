package com.example.csc567_project.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.apiKeyDataStore: DataStore<Preferences> by preferencesDataStore(name = "api_keys")

class ApiKeyStore(private val context: Context) {

    // Save API key for a specific model
    suspend fun saveApiKey(modelName: String, apiKey: String) {
        val key = stringPreferencesKey("apiKey_$modelName")
        context.apiKeyDataStore.edit { prefs ->
            prefs[key] = apiKey
        }
    }

    // Read API key for a specific model
    fun getApiKey(modelName: String): Flow<String> {
        val key = stringPreferencesKey("apiKey_$modelName")
        return context.apiKeyDataStore.data.map { prefs ->
            prefs[key] ?: ""
        }
    }
}