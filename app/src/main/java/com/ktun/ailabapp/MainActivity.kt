package com.ktun.ailabapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ktun.ailabapp.presentation.ui.screens.login.LoginScreen
import com.ktun.ailabapp.ui.theme.AiLabAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiLabAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLoginSuccess = {
                            // Navigation buraya gelecek
                        }
                    )
                }
            }
        }
    }
}