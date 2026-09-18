package com.ktun.ailabapp.presentation.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktun.ailabapp.R
import com.ktun.ailabapp.presentation.ui.components.buttons.AiLabButton
import com.ktun.ailabapp.presentation.ui.components.buttons.AiLabButtonVariant
import com.ktun.ailabapp.presentation.ui.components.inputs.AiLabTextField
import com.ktun.ailabapp.ui.theme.AiLabTheme
import com.ktun.ailabapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.35f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                Image(
                    painter = painterResource(id = R.drawable.ai_lab_logo_in),
                    contentDescription = "AI Lab Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(top = AppSpacing.sm),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.xxxl))

            Text(
                text = "Ai Lab'e Hoşgeldin",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (AiLabTheme.isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = AppSpacing.xxs)
            )

            Text(
                text = "Hesabına giriş yap",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = AppSpacing.xxl)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xxxl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                AiLabTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    label = "E-posta",
                    placeholder = "E-posta adresinizi girin",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                AiLabTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    label = "Şifre",
                    placeholder = "Şifrenizi girin",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.login(onSuccess = onLoginSuccess) }
                    )
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xxxl),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleRememberMe() }
                ) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe() },
                        colors = if (AiLabTheme.isDark) {
                            CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkmarkColor = Color.White,
                            )
                        } else {
                            CheckboxDefaults.colors()
                        }
                    )
                    Text(
                        "Beni Hatırla",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val linkInteractionSource = remember { MutableInteractionSource() }
                val isLinkPressed by linkInteractionSource.collectIsPressedAsState()
                TextButton(
                    onClick = {
                        viewModel.sendPasswordResetEmail { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    interactionSource = linkInteractionSource,
                    colors = if (AiLabTheme.isDark) {
                        ButtonDefaults.textButtonColors(
                            contentColor = if (isLinkPressed) MaterialTheme.colorScheme.onSurfaceVariant
                                           else AiLabTheme.extendedColors.focusAccent
                        )
                    } else {
                        ButtonDefaults.textButtonColors()
                    }
                ) {
                    Text(
                        "Şifremi Unuttum",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            AiLabButton(
                text = "Giriş Yap",
                onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier.padding(horizontal = AppSpacing.xxxl)
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            AiLabButton(
                text = "Hesabın Yok Mu? Kaydol",
                onClick = onNavigateToRegister,
                variant = AiLabButtonVariant.Secondary,
                modifier = Modifier.padding(horizontal = AppSpacing.xxxl)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Yapay Zeka ve Veri Bilimi Laboratuvarı, D114",
                style = MaterialTheme.typography.labelSmall,
                color = if (AiLabTheme.isDark) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = AppSpacing.xxxl, top = AppSpacing.lg)
            )
        }
    }
}
