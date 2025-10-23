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
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")
        private val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
    }

    // Token İşlemleri
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    // Kullanıcı Bilgileri
    suspend fun saveUserData(
        userId: String,
        email: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_FIRST_NAME_KEY] = firstName
            preferences[USER_LAST_NAME_KEY] = lastName
            preferences[USER_PHONE_KEY] = phone
        }
    }

    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { it[USER_ID_KEY] }
    }

    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { it[USER_EMAIL_KEY] }
    }

    fun getUserFirstName(): Flow<String?> {
        return context.dataStore.data.map { it[USER_FIRST_NAME_KEY] }
    }

    fun getUserLastName(): Flow<String?> {
        return context.dataStore.data.map { it[USER_LAST_NAME_KEY] }
    }

    fun getUserPhone(): Flow<String?> {
        return context.dataStore.data.map { it[USER_PHONE_KEY] }
    }

    // Tüm Verileri Temizle (Logout)
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Giriş Yapılmış mı Kontrolü
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            !preferences[TOKEN_KEY].isNullOrEmpty()
        }
    }
}