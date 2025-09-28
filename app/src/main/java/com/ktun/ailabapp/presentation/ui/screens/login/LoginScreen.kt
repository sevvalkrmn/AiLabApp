package com.ktun.ailabapp.presentation.ui.screens.login

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktun.ailabapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // ÜST KISIM - Gradient + Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D24D8), // Açık mavi
                            Color(0xFF071372)  // Koyu mavi
                        ),
                        radius = 600f,
                        center = Offset(0.2f, 0.8f) // Sol alt odaklı
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
                Image(
                    painter = painterResource(id = R.drawable.ailablogo),
                    contentDescription = "AI Lab Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(16.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )

        }

        // ALT KISIM - Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color(0xFFF1F1FC))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ai Lab'e Hoşgeldin",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF07137F)
            )

            Text(
                text = "Hesabına giriş yap",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email TextField - ViewModel'e bağlı
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Mail'ini Gir") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF07137F),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF07137F),
                    unfocusedLabelColor = Color(0xFF9E9E9E),
                    focusedContainerColor = Color.White,  // containerColor yerine
                    unfocusedContainerColor = Color.White // containerColor yerine
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField - ViewModel'e bağlı
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Şifreni Gir") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Şifreyi göster/gizle",
                            tint = Color(0xFF07137F)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF07137F),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF07137F),
                    unfocusedLabelColor = Color(0xFF9E9E9E),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Remember me + Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe() },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF07137F))
                    )
                    Text("Beni hatırla", fontSize = 14.sp)
                }

                TextButton(onClick = { }) {
                    Text(
                        "Şifreni mi unuttun?",
                        color = Color(0xFF07137F),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button - ViewModel'e bağlı
            Button(
                onClick = viewModel::login,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF07137F)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Giriş Yap",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // Error Message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Yapay Zeka ve Veri Bilimi Laboratuvarı, D14",
                fontSize = 12.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}