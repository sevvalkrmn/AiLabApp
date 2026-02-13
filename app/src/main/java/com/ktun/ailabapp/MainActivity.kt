package com.ktun.ailabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // ✅ Import added
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktun.ailabapp.presentation.ui.screens.navigation.Screen
import com.ktun.ailabapp.presentation.ui.screens.splash.SplashScreen
import com.ktun.ailabapp.ui.theme.AiLabAppTheme
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
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
        installSplashScreen() // ✅ Call before super.onCreate
        super.onCreate(savedInstanceState)

        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination by mainViewModel.startDestination.collectAsState()
                    val isLoading by mainViewModel.isLoading.collectAsState()
                    var splashFinished by remember { mutableStateOf(false) }

                    LaunchedEffect(startDestination) {
                        if (startDestination == Screen.Login.route) {
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute != null && currentRoute != Screen.Login.route) {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }

                    val showSplash = !splashFinished || isLoading || startDestination == null

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                        },
                        label = "splash_transition"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(onFinished = { splashFinished = true })
                        } else {
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination!!
                            )
                        }
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
                Logger.d("onStop: Clearing session (RememberMe=false)", "MainActivity")
                authManager.signOut()
                preferencesManager.clearAllData()
            }
        }
    }
}
