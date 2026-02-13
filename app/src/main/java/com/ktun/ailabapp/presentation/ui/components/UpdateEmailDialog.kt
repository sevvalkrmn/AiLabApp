package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.Icons // ✅ Import added
import androidx.compose.material.icons.filled.Visibility // ✅ Import added
import androidx.compose.material.icons.filled.VisibilityOff // ✅ Import added
import androidx.compose.ui.text.input.VisualTransformation // ✅ Import added

@Composable
fun UpdateEmailDialog(
    onDismiss: () -> Unit,
    onConfirm: (password: String, newEmail: String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) } // ✅ Visibility State

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("E-posta Adresini Değiştir") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mevcut Şifre") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), // ✅ Toggle
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = { // ✅ Eye Icon
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Gizle" else "Göster"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Yeni E-posta Adresi") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
// ...
            Button(
                onClick = { onConfirm(password, newEmail) },
                enabled = password.isNotBlank() && newEmail.isNotBlank()
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
