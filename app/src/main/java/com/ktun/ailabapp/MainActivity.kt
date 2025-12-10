package com.ktun.ailabapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktun.ailabapp.ui.theme.AiLabAppTheme
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var authRepository: AuthRepository

    private var isHandlingSessionExpired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            authRepository.sessionExpiredEvent.collect {
                if (isHandlingSessionExpired) {
                    Log.d("MainActivity", "⏭️ Already handling session expired, skipping")
                    return@collect
                }

                isHandlingSessionExpired = true
                Log.e("MainActivity", "🔴 Session expired event received - Restarting app")

                preferencesManager.clearAllData()
                recreate()
            }
        }

        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        Log.d("AILAB_DEBUG", "=== APP STARTING ===")

                        val rememberMe = preferencesManager.getRememberMe().first()
                        val token = preferencesManager.getToken().first()

                        Log.d("AILAB_DEBUG", """
                            RememberMe: $rememberMe
                            Token exists: ${!token.isNullOrEmpty()}
                        """.trimIndent())

                        startDestination = if (!token.isNullOrEmpty()) {
                            Log.d("AILAB_DEBUG", "Token found - Going to HOME")
                            Screen.Home.route
                        } else {
                            Log.d("AILAB_DEBUG", "No token - Going to LOGIN")
                            Screen.Login.route
                        }
                    }

                    if (startDestination != null) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // ✅ GÜNCELLEME: onStop() kullan (uygulama arka plana gidince)
    override fun onStop() {
        super.onStop()

        lifecycleScope.launch {
            val rememberMe = preferencesManager.getRememberMe().first()

            Log.d("MainActivity", "📱 App going to background - RememberMe: $rememberMe")

            if (!rememberMe) {
                Log.d("MainActivity", "🧹 RememberMe is FALSE - Clearing tokens")
                preferencesManager.clearToken()
                preferencesManager.clearRefreshToken()
            } else {
                Log.d("MainActivity", "💾 RememberMe is TRUE - Tokens preserved")
            }
        }
    }
}