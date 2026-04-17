package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ktun.ailabapp.ui.theme.ErrorRed

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
                    color = ErrorRed,
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
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
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

