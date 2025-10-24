package com.ktun.ailabapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val FIRST_NAME_KEY = stringPreferencesKey("first_name")
        private val LAST_NAME_KEY = stringPreferencesKey("last_name")
        private val PHONE_KEY = stringPreferencesKey("phone")
    }

    // Token okuma
    fun getToken(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Token kaydetme
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // RefreshToken okuma
    fun getRefreshToken(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    // Refresh token kaydetme
    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // Kullanıcı bilgilerini kaydetme
    suspend fun saveUserData(
        userId: String,
        email: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[EMAIL_KEY] = email
            preferences[FIRST_NAME_KEY] = firstName
            preferences[LAST_NAME_KEY] = lastName
            preferences[PHONE_KEY] = phone
        }
    }

    // Tüm verileri temizle
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}