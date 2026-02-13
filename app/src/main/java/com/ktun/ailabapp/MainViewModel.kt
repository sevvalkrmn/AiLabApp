package com.ktun.ailabapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.presentation.ui.screens.navigation.Screen
import com.ktun.ailabapp.util.FirebaseAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authManager: FirebaseAuthManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkSession()
        observeSessionExpiry()
    }

    private fun observeSessionExpiry() {
        viewModelScope.launch {
            authRepository.sessionExpiredEvent.collect {
                android.util.Log.e("MainViewModel", "🔴 Session Expired Event Received")
                preferencesManager.clearAllData()
                authManager.signOut()
                _startDestination.value = Screen.Login.route
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                // Küçük bir gecikme ekleyelim (yarış koşullarını önlemek için)
                delay(500) 

                val rememberMe = preferencesManager.getRememberMe().first()
                val firebaseUser = authManager.currentUser

                android.util.Log.d("MainViewModel", "CheckSession: RememberMe=$rememberMe, User=${firebaseUser?.uid}")

                if (!rememberMe && firebaseUser != null) {
                    android.util.Log.d("MainViewModel", "RememberMe FALSE -> Signing Out")
                    authManager.signOut()
                    preferencesManager.clearAllData()
                    _startDestination.value = Screen.Login.route
                } else if (firebaseUser != null) {
                    android.util.Log.d("MainViewModel", "User Found -> Home")
                    _startDestination.value = Screen.Home.route
                } else {
                    android.util.Log.d("MainViewModel", "No User -> Login")
                    _startDestination.value = Screen.Login.route
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Session check error", e)
                _startDestination.value = Screen.Login.route
            } finally {
                _isLoading.value = false
            }
        }
    }
}
