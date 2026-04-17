package com.ktun.ailabapp.presentation.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktun.ailabapp.R
import com.ktun.ailabapp.presentation.ui.components.buttons.GradientButton
import com.ktun.ailabapp.ui.theme.*

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
                        BackgroundLight,
                        BackgroundLight,
                        BackgroundLight,
                        GradientStart,
                        GradientMid,
                        GradientEnd,
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
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = AppSpacing.xxs)
            )

            Text(
                text = "Hesabına giriş yap",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier.padding(bottom = AppSpacing.xxl)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xxxl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                RoundedInput(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    placeholder = "E-posta adresinizi girin",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                RoundedInput(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    placeholder = "Şifrenizi girin",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isPassword = true,
                    isPasswordVisible = uiState.isPasswordVisible,
                    onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                    onDone = { viewModel.login(onSuccess = onLoginSuccess) }
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = SecondaryBlue,
                            uncheckedColor = LabelGray
                        )
                    )
                    Text(
                        "Beni Hatırla",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                TextButton(onClick = {
                    viewModel.sendPasswordResetEmail { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text(
                        "Şifremi Unuttum",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            GradientButton(
                text = "Giriş Yap",
                onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier.padding(horizontal = AppSpacing.xxxl)
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            GradientButton(
                text = "Hesabın Yok Mu? Kaydol",
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(horizontal = AppSpacing.xxxl)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Yapay Zeka ve Veri Bilimi Laboratuvarı, D114",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = AppSpacing.xxxl, top = AppSpacing.lg)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = LabelGray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightLarge),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BorderGray,
            unfocusedBorderColor = BorderGray,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            cursorColor = SecondaryBlue
        ),
        visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = LabelGray
                    )
                }
            }
        } else null,
        singleLine = true
    )
}
