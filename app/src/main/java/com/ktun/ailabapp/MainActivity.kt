package com.ktun.ailabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.navigation.Screen
import com.ktun.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktun.ailabapp.ui.theme.AiLabAppTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(this)

        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    // Token kontrolü yap
                    LaunchedEffect(Unit) {
                        val token = preferencesManager.getToken().first()

                        // DEBUG LOG
                        android.util.Log.d("MainActivity", "Token kontrolü: ${if (token.isNullOrEmpty()) "YOK" else "VAR"}")
                        android.util.Log.d("MainActivity", "Token değeri: ${token?.take(20)}...")

                        startDestination = if (token.isNullOrEmpty()) {
                            android.util.Log.d("MainActivity", "Login ekranına yönlendiriliyor")
                            Screen.Login.route
                        } else {
                            android.util.Log.d("MainActivity", "Home ekranına yönlendiriliyor")
                            Screen.Home.route
                        }
                    }

                    // StartDestination belirlenene kadar bekle
                    startDestination?.let { destination ->
                        NavGraph(
                            navController = navController,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}