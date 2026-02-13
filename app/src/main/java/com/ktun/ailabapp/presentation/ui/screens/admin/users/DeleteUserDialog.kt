package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DeleteUserDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kullanıcıyı Sil") },
        text = {
            Column {
                Text(
                    text = "⚠️ Bu işlem geri alınamaz!",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "\"$userName\" adlı kullanıcıyı sistemden silmek istediğinize emin misiniz?")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Bu işlem, kullanıcının tüm verilerini (puanlar, projeler, görevler) silecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

