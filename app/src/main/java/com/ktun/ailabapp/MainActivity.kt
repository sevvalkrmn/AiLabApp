package com.ktunailab.ailabapp

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
import androidx.navigation.compose.rememberNavController
import com.ktunailab.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktunailab.ailabapp.ui.theme.AiLabAppTheme
import com.ktunailab.ailabapp.data.local.datastore.PreferencesManager
import com.ktunailab.ailabapp.presentation.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    var isLoading by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        Log.d("AILAB_DEBUG", "LaunchedEffect başlıyor...")
                        val token = preferencesManager.getToken().first()
                        Log.d("AILAB_DEBUG", "Token alındı: $token")
                        startDestination = if (token.isNullOrEmpty()) {
                            Screen.Login.route
                        } else {
                            Screen.Home.route
                        }
                        isLoading = false
                        Log.d("AILAB_DEBUG", "startDestination ayarlandı: $startDestination")
                    }

                    if (startDestination != null) {
                        // Hedef rota belliyse NavGraph'ı göster
                        Log.d("AILAB_DEBUG", "NavGraph çiziliyor. Rota: $startDestination")
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                    } else {
                        // Hedef rota henüz belirlenmediyse yüklenme ekranını göster
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
}