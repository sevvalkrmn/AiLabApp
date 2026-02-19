package com.ktun.ailabapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.presentation.ui.screens.navigation.NavGraph
import com.ktun.ailabapp.presentation.ui.screens.navigation.Screen
import com.ktun.ailabapp.presentation.ui.screens.splash.SplashScreen
import com.ktun.ailabapp.ui.theme.AiLabAppTheme
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationEvent(
    val type: String,
    val referenceId: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var authManager: FirebaseAuthManager

    private val mainViewModel: MainViewModel by viewModels()

    private val _notificationEvent = MutableStateFlow<NotificationEvent?>(null)

    // Android 13+ bildirim izni launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Logger.d("Bildirim izni verildi", "MainActivity")
        } else {
            Logger.w("Bildirim izni reddedildi", "MainActivity")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Android 13+ bildirim izni iste
        requestNotificationPermission()

        // Bildirime tiklanarak acildiysa verileri al
        handleNotificationIntent(intent)

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
                    val context = LocalContext.current
                    val notificationEvent by _notificationEvent.collectAsState()

                    // Oturum süresi doldu bildirimi
                    LaunchedEffect(Unit) {
                        mainViewModel.sessionExpiredMessage.collect {
                            Toast.makeText(
                                context,
                                "Oturum süresi doldu. Lütfen tekrar giriş yapın.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    // Login ekranına yönlendirme
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

                    // Bildirim tiklanma yonlendirmesi
                    LaunchedEffect(notificationEvent) {
                        val event = notificationEvent ?: return@LaunchedEffect

                        // Navigasyon hazir olana kadar bekle
                        while (navController.currentDestination == null) {
                            delay(100)
                        }

                        navigateToNotification(navController, event)
                        _notificationEvent.value = null
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        // Foreground bildirimlerimiz "notification_type" / "reference_id" key'leri kullanir
        // Background'da sistem "type" / "referenceId" key'lerini (FCM data payload) kullanir
        val type = intent.getStringExtra("notification_type")
            ?: intent.getStringExtra("type")
            ?: return
        val referenceId = intent.getStringExtra("reference_id")
            ?: intent.getStringExtra("referenceId")

        Logger.d("Bildirim tiklandi - type: $type, referenceId: $referenceId", "MainActivity")

        _notificationEvent.value = NotificationEvent(type, referenceId)

        // Intent extras'lari temizle (tekrar tetiklenmemesi icin)
        intent.removeExtra("notification_type")
        intent.removeExtra("reference_id")
        intent.removeExtra("type")
        intent.removeExtra("referenceId")
    }

    private fun navigateToNotification(navController: NavHostController, event: NotificationEvent) {
        when (event.type) {
            "task" -> {
                // Task bildirimi -> Home sayfasina task detay dialog ile yonlendir
                navController.navigate(Screen.Home.createRoute(taskId = event.referenceId)) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
            "announcement" -> {
                // Duyuru bildirimi -> Duyurular sayfasina yonlendir
                navController.navigate(Screen.Announcements.route)
            }
            "auto_checkout_warning" -> {
                // Lab uyarisi -> Home sayfasina yonlendir
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
