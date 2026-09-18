package com.ktun.ailabapp.presentation.ui.screens.register

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktun.ailabapp.R
import com.ktun.ailabapp.presentation.ui.components.buttons.AiLabButton
import com.ktun.ailabapp.presentation.ui.components.inputs.AiLabTextField
import com.ktun.ailabapp.presentation.ui.screens.register.RegisterViewModel
import com.ktun.ailabapp.ui.theme.AiLabTheme
import com.ktun.ailabapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    BackHandler(enabled = uiState.step == 2) {
        viewModel.previousStep()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
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
                text = "Hesap Oluştur",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (AiLabTheme.isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = AppSpacing.xxs)
            )

            Text(
                text = if (uiState.step == 1) "Adım 1/2: Giriş Bilgileri" else "Adım 2/2: Kişisel Bilgiler",
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
                if (uiState.step == 1) {
                    val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
                    val isValidPassword = passwordRegex.matches(uiState.password)
                    val passwordsMatch = uiState.password == uiState.confirmPassword && uiState.password.isNotBlank()

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
                        placeholder = "Şifre belirleyin",
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        isError = uiState.password.isNotBlank() && !isValidPassword,
                        errorMessage = "En az 8 karakter, 1 büyük harf ve 1 rakam gereklidir."
                    )

                    AiLabTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        label = "Şifre (Tekrar)",
                        placeholder = "Şifrenizi tekrar girin",
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        isError = uiState.confirmPassword.isNotBlank() && !passwordsMatch,
                        errorMessage = "Şifreler uyuşmuyor."
                    )

                } else {
                    AiLabTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::updateFullName,
                        label = "Ad",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    AiLabTextField(
                        value = uiState.surname,
                        onValueChange = viewModel::updateSurname,
                        label = "Soyad",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    AiLabTextField(
                        value = uiState.username,
                        onValueChange = viewModel::updateUsername,
                        label = "Kullanıcı Adı",
                        placeholder = "min. 3 karakter",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    AiLabTextField(
                        value = uiState.schoolNumber,
                        onValueChange = viewModel::updateSchoolNumber,
                        label = "Okul Numarası",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )

                    val phoneRegex = "^5[0-9]{9}$".toRegex()
                    val isValidPhone = phoneRegex.matches(uiState.phone)

                    AiLabTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::updatePhone,
                        label = "Telefon",
                        placeholder = "5xx xxx xx xx",
                        prefix = "+90 ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        isError = uiState.phone.isNotBlank() && !isValidPhone,
                        errorMessage = "Telefon numarası 5 ile başlamalı ve 10 haneli olmalıdır."
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
            val isStep1Valid = uiState.email.isNotBlank() &&
                    passwordRegex.matches(uiState.password) &&
                    uiState.password == uiState.confirmPassword
            val isStep2Valid = uiState.fullName.isNotBlank() &&
                    uiState.surname.isNotBlank() &&
                    uiState.username.length >= 3 &&
                    uiState.schoolNumber.isNotBlank() &&
                    uiState.phone.matches("^5[0-9]{9}$".toRegex())
            val isButtonEnabled = if (uiState.step == 1) isStep1Valid else isStep2Valid

            AiLabButton(
                text = if (uiState.step == 1) "Devam Et" else "Kayıt Ol",
                onClick = {
                    if (uiState.step == 1) viewModel.createFirebaseUser()
                    else viewModel.completeRegistration(onSuccess = onRegisterSuccess)
                },
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading && isButtonEnabled,
                modifier = Modifier.padding(horizontal = AppSpacing.xxxl)
            )

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zaten hesabınız var mı? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Giriş Yap",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (AiLabTheme.isDark) AiLabTheme.extendedColors.focusAccent else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

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
