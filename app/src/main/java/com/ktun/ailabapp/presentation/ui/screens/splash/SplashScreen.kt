package com.ktun.ailabapp.presentation.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ktun.ailabapp.R
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.SFProFont
import com.ktun.ailabapp.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Logo scale animasyonu
    val logoScale = remember { Animatable(0.6f) }
    // Logo alpha animasyonu
    val logoAlpha = remember { Animatable(0f) }
    // Alt yazı görünürlüğü
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Logo scale + fade aynı anda
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Logo geldikten sonra yazı belirsin
        delay(500)
        showSubtitle = true

        // Toplam süre sonra bitir
        delay(1000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ailablogo),
                contentDescription = "AI Lab Logo",
                modifier = Modifier
                    .size(220.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                colorFilter = ColorFilter.tint(White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(tween(500))
            ) {
                Text(
                    text = "KTUN AI LAB",
                    fontFamily = SFProFont,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = White.copy(alpha = 0.8f),
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
