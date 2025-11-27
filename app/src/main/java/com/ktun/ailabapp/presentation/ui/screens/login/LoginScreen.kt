package com.ktunailab.ailabapp.presentation.ui.screens.login

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktunailab.ailabapp.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.ImeAction
import com.ktunailab.ailabapp.ui.theme.PrimaryBlue
import com.ktunailab.ailabapp.ui.theme.SecondaryBlue
import com.ktunailab.ailabapp.ui.theme.BackgroundLight
import com.ktunailab.ailabapp.ui.theme.TextGray
import com.ktunailab.ailabapp.ui.theme.BorderGray
import com.ktunailab.ailabapp.ui.theme.LabelGray
import com.ktunailab.ailabapp.ui.theme.White

// LoginScreen fonksiyonundan ÖNCE tanımla
private val sfProFontFamily = FontFamily(
    Font(R.font.sfpro_regular, FontWeight.Normal),
    Font(R.font.sfpro_medium, FontWeight.Medium),
    Font(R.font.sfpro_bold, FontWeight.Bold)
)

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
    val screenWidth = configuration.screenWidthDp.dp

    // Error mesajını göster
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Giriş başarılı olduğunda
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // ÜST KISIM - Gradient + Logo (Ekranın %40'ı)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.4f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SecondaryBlue,
                            PrimaryBlue
                        ),
                        radius = screenWidth.value * 1.5f,
                        center = Offset(0.2f, 0.8f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ailablogo),
                contentDescription = "AI Lab Logo",
                modifier = Modifier
                    .size(screenWidth * 0.5f)
                    .padding(screenWidth * 0.04f),
                colorFilter = ColorFilter.tint(White)
            )
        }

        // ALT KISIM - Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = screenWidth * 0.08f, topEnd = screenWidth * 0.08f))
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(screenWidth * 0.06f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.03f))

            Text(
                text = "Ai Lab'e Hoşgeldin",
                fontSize = (screenWidth.value * 0.06f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sfProFontFamily,
                color = PrimaryBlue
            )

            Text(
                text = "Hesabına giriş yap",
                fontSize = (screenWidth.value * 0.035f).sp,
                color = TextGray,
                fontWeight = FontWeight.Medium,
                fontFamily = sfProFontFamily,
                modifier = Modifier.padding(top = screenHeight * 0.005f)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.04f))

            // Email TextField
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Mail'ini Gir", fontSize = (screenWidth.value * 0.04f).sp) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { }
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = LabelGray,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(screenWidth * 0.03f),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = (screenWidth.value * 0.04f).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            // Password TextField
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Şifreni Gir", fontSize = (screenWidth.value * 0.04f).sp) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.login(onSuccess = onLoginSuccess)
                    }
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Şifreyi göster/gizle",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(screenWidth * 0.06f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = LabelGray,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(screenWidth * 0.03f),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = (screenWidth.value * 0.04f).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

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
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue),
                        modifier = Modifier.size(screenWidth * 0.06f)
                    )
                    Text(
                        "Beni hatırla",
                        fontSize = (screenWidth.value * 0.035f).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = sfProFontFamily
                    )
                }

                TextButton(onClick = { }) {
                    Text(
                        "Şifreni mi unuttun?",
                        color = PrimaryBlue,
                        fontSize = (screenWidth.value * 0.035f).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = sfProFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.03f))

            // Login Button
            Button(
                onClick = {
                    viewModel.login(onSuccess = onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.07f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(screenWidth * 0.03f),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(screenWidth * 0.05f),
                        color = White
                    )
                } else {
                    Text(
                        "Giriş Yap",
                        fontSize = (screenWidth.value * 0.04f).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = sfProFontFamily,
                        color = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.015f))

            // Kaydol Butonu
            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.07f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue
                ),
                border = BorderStroke(screenWidth * 0.005f, PrimaryBlue),
                shape = RoundedCornerShape(screenWidth * 0.03f)
            ) {
                Text(
                    text = "Hesabın yok mu? Kaydol!",
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = sfProFontFamily,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Text(
                text = "Yapay Zeka ve Veri Bilimi Laboratuvarı, D114",
                fontSize = (screenWidth.value * 0.03f).sp,
                color = TextGray,
                fontWeight = FontWeight.Medium,
                fontFamily = sfProFontFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))
        }
    }
}