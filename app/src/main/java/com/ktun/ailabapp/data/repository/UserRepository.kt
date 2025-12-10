package com.ktun.ailabapp.data.repository

import com.google.firebase.appdistribution.gradle.ApiService
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    // User işlemleri buraya gelecek
}