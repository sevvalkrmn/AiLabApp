package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DebugButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp)  // Sadece size, arka plan yok
    ) {
        Icon(
            Icons.Default.BugReport,
            contentDescription = "Hata Bildir",
            tint = Color(0xFFF4F6FC),  // Açık gri/beyaz renk
            modifier = Modifier.size(28.dp)  // İkon boyutu
        )
    }
}