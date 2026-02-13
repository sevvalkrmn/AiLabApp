package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (oldPass: String, newPass: String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPassVisible by remember { mutableStateOf(false) }
    var newPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    // Şifre kuralları: Min 8 karakter, en az 1 büyük harf, en az 1 rakam
    val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
    val isValidPassword = passwordRegex.matches(newPassword)

    val isFormValid = oldPassword.isNotBlank() && 
                     isValidPassword && 
                     newPassword == confirmPassword

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Şifreyi Değiştir") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Eski Şifre
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Mevcut Şifre") },
                    visualTransformation = if (oldPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { oldPassVisible = !oldPassVisible }) {
                            Icon(imageVector = if (oldPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Yeni Şifre
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Yeni Şifre") },
                    visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { newPassVisible = !newPassVisible }) {
                            Icon(imageVector = if (newPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = newPassword.isNotBlank() && !isValidPassword
                )

                if (newPassword.isNotBlank() && !isValidPassword) {
                    Text(
                        "Şifre en az 8 karakter, 1 büyük harf ve 1 rakam içermelidir.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }

                // Yeni Şifre Tekrar
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Yeni Şifre Tekrar") },
                    visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                            Icon(imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.isNotBlank() && newPassword != confirmPassword
                )

                if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                    Text("Şifreler uyuşmuyor", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(oldPassword, newPassword) },
                enabled = isFormValid
            ) {
                Text("Güncelle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
