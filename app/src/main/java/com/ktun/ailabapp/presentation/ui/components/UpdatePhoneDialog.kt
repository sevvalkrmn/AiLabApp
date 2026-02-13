package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun UpdatePhoneDialog(
    onDismiss: () -> Unit,
    onConfirm: (newPhone: String) -> Unit
) {
    var newPhone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Telefon Numarasını Değiştir") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPhone,
                    onValueChange = { newPhone = it },
                    label = { Text("Yeni Telefon Numarası (5xx xxx xx xx)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("+90 ") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm("+90 $newPhone") },
                enabled = newPhone.length >= 10
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
