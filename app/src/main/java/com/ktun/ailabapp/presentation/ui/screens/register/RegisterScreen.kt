package com.ktun.ailabapp.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktun.ailabapp.presentation.ui.screens.register.RegisterViewModel
import com.ktun.ailabapp.R
import com.ktun.ailabapp.ui.theme.PrimaryBlue

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

    // Back tuşuna basıldığında adım 2'den 1'e dön
    BackHandler(enabled = uiState.step == 2) {
        viewModel.previousStep()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
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
                        Color(0xFFF4F6FC), // Top - light
                        Color(0xFFF4F6FC), // Keep light
                        Color(0xFFF4F6FC), // Still light
                        Color(0xFFE8E8EC), // Slight transition
                        Color(0xFFD4D4D8), // Mid gray
                        Color(0xFFC0C0C4)  // Bottom - light gray
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
            // --- TOP SECTION (Same as Login) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.35f),
                contentAlignment = Alignment.Center
            ) {
                // Background shape
                Image(
                    painter = painterResource(id = R.drawable.login_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // Logo with inner shadow
                Image(
                    painter = painterResource(id = R.drawable.ai_lab_logo_in),
                    contentDescription = "AI Lab Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TITLE SECTION ---
            Text(
                text = "Hesap Oluştur",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (uiState.step == 1) "Adım 1/2: Giriş Bilgileri" else "Adım 2/2: Kişisel Bilgiler",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- FORM SECTION ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.step == 1) {
                    // --- STEP 1: Email & Password ---

                    // Şifre kuralları: Min 8 karakter, en az 1 büyük harf, en az 1 rakam
                    val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
                    val isValidPassword = passwordRegex.matches(uiState.password)
                    val passwordsMatch = uiState.password == uiState.confirmPassword && uiState.password.isNotBlank()

                    RegisterInput(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail,
                        placeholder = "E-posta adresinizi girin",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    RegisterInput(
                        value = uiState.password,
                        onValueChange = viewModel::updatePassword,
                        placeholder = "Şifre belirleyin",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                        isPassword = true,
                        isPasswordVisible = uiState.isPasswordVisible,
                        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                        isError = uiState.password.isNotBlank() && !isValidPassword
                    )

                    if (uiState.password.isNotBlank() && !isValidPassword) {
                        Text(
                            text = "En az 8 karakter, 1 büyük harf ve 1 rakam gereklidir.",
                            color = Color.Red,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    RegisterInput(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        placeholder = "Şifrenizi tekrar girin",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isPassword = true,
                        isPasswordVisible = uiState.isConfirmPasswordVisible,
                        onTogglePasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                        isError = uiState.confirmPassword.isNotBlank() && !passwordsMatch
                    )

                    if (uiState.confirmPassword.isNotBlank() && !passwordsMatch) {
                        Text(
                            text = "Şifreler uyuşmuyor.",
                            color = Color.Red,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                } else {
                    // --- STEP 2: Personal Details ---

                    RegisterInput(
                        value = uiState.fullName,
                        onValueChange = viewModel::updateFullName,
                        placeholder = "Ad Soyad",
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )

                    RegisterInput(
                        value = uiState.username,
                        onValueChange = viewModel::updateUsername,
                        placeholder = "Kullanıcı Adı (min: 3 karakter)",
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )

                    RegisterInput(
                        value = uiState.schoolNumber,
                        onValueChange = viewModel::updateSchoolNumber,
                        placeholder = "Okul Numarası",
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )

                    RegisterInput(
                        value = uiState.phone,
                        onValueChange = viewModel::updatePhone,
                        placeholder = "Telefon (5xx xxx xx xx)",
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                        prefix = "+90 "
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- ACTION BUTTON ---
            val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
            val isStep1Valid = uiState.email.isNotBlank() && 
                              passwordRegex.matches(uiState.password) && 
                              uiState.password == uiState.confirmPassword
            
            val isStep2Valid = uiState.fullName.isNotBlank() && 
                              uiState.username.length >= 3 && 
                              uiState.schoolNumber.isNotBlank() && 
                              uiState.phone.length >= 10

            val isButtonEnabled = if (uiState.step == 1) isStep1Valid else isStep2Valid

            Button(
                onClick = {
                    if (uiState.step == 1) {
                        viewModel.createFirebaseUser()
                    } else {
                        viewModel.completeRegistration(onSuccess = onRegisterSuccess)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color(0xFF0D24D8).copy(alpha = 0.3f),
                        spotColor = Color(0xFF0D24D8).copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                enabled = !uiState.isLoading && isButtonEnabled
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0D24D8),
                                    PrimaryBlue
                                ),
                                center = Offset(0.5f, 0.5f),
                                radius = 800f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (uiState.step == 1) "Devam Et" else "Kayıt Ol",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FOOTER (Login Link) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zaten hesabınız var mı? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Giriş Yap",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D24D8),
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
                fontSize = 11.sp,
                color = Color(0xFF1E3A8A),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
    prefix: String? = null,
    isError: Boolean = false // ✅ Parametre eklendi
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError, // ✅ Atama yapıldı
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
        },
        prefix = if (prefix != null) {
            { Text(prefix, color = Color(0xFF1E293B), fontSize = 13.sp) }
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else Color(0xFFE2E8F0), // ✅ Hata rengi
            unfocusedBorderColor = if (isError) Color.Red else Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF1E293B),
            unfocusedTextColor = Color(0xFF1E293B),
            cursorColor = Color(0xFF0D24D8)
        ),
        visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
        } else null,
        singleLine = true
    )
}