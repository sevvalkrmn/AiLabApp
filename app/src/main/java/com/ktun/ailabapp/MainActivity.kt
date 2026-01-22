package com.ktun.ailabapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktun.ailabapp.presentation.ui.screens.navigation.Screen
import com.ktun.ailabapp.ui.theme.AiLabAppTheme
import com.ktun.ailabapp.util.FirebaseAuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var authManager: FirebaseAuthManager

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "🎬 onCreate called")

        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination by mainViewModel.startDestination.collectAsState()
                    val isLoading by mainViewModel.isLoading.collectAsState()

                    // ✅ Sadece Session Expired durumunda zorunlu yönlendirme yap
                    LaunchedEffect(startDestination) {
                        if (startDestination == Screen.Login.route) {
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute != null && currentRoute != Screen.Login.route) {
                                Log.d("MainActivity", "🔄 Navigating to Login due to session change")
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }

                    if (isLoading || startDestination == null) {
                        Log.d("MainActivity", "⏳ Showing loading indicator")
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Log.d("MainActivity", "✅ Creating NavGraph with: $startDestination")
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            val rememberMe = preferencesManager.getRememberMe().first()
            if (!rememberMe) {
                Log.d("MainActivity", "onStop: Clearing session (RememberMe=false)")
                authManager.signOut()
                preferencesManager.clearAllData()
            }
        }
    }
}
