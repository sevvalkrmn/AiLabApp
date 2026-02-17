package com.ktun.ailabapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.screens.navigation.Screen
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authManager: FirebaseAuthManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sessionExpiredMessage = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredMessage: SharedFlow<Unit> = _sessionExpiredMessage.asSharedFlow()

    init {
        checkSession()
        observeSessionExpiry()
    }

    private fun observeSessionExpiry() {
        viewModelScope.launch {
            sessionManager.sessionExpiredEvent.collect {
                Logger.e("Session Expired Event Received", tag = "MainViewModel")
                _sessionExpiredMessage.tryEmit(Unit)
                preferencesManager.clearAllData()
                authManager.signOut()
                _startDestination.value = Screen.Login.route
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                // Firebase Auth state hazır olana kadar reaktif bekle
                val firebaseUser = authManager.awaitCurrentUser()
                val rememberMe = preferencesManager.getRememberMe().first()

                if (!rememberMe && firebaseUser != null) {
                    authManager.signOut()
                    preferencesManager.clearAllData()
                    _startDestination.value = Screen.Login.route
                } else if (firebaseUser != null) {
                    _startDestination.value = Screen.Home.route
                } else {
                    _startDestination.value = Screen.Login.route
                }
            } catch (e: Exception) {
                Logger.e("Session check error", e, "MainViewModel")
                _startDestination.value = Screen.Login.route
            } finally {
                _isLoading.value = false
            }
        }
    }
}
