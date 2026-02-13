package com.ktun.ailabapp.presentation.ui.screens.admin.users.score

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktun.ailabapp.presentation.ui.screens.admin.score.AdjustScoreViewModel
import com.ktun.ailabapp.ui.theme.PrimaryBlue

@Composable
fun AdjustScoreDialog(
    userId: String,
    userName: String,
    currentScore: Double, // ✅ Int -> Double
    onDismiss: () -> Unit,
    viewModel: AdjustScoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ Dialog açıldığında veya userId değiştiğinde state'i sıfırla
    LaunchedEffect(userId) {
        viewModel.resetState()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            kotlinx.coroutines.delay(500)
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Puan Ekle / Azalt",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                // User Info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mevcut Puan: $currentScore",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Add/Subtract Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ToggleButton(
                        text = "Ekle",
                        isSelected = uiState.isAdding,
                        onClick = {
                            if (!uiState.isAdding) viewModel.toggleAddSubtract()
                        },
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF4CAF50)
                    )
                    ToggleButton(
                        text = "Azalt",
                        isSelected = !uiState.isAdding,
                        onClick = {
                            if (uiState.isAdding) viewModel.toggleAddSubtract()
                        },
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFF5252)
                    )
                }

                // Score Input
                OutlinedTextField(
                    value = uiState.scoreInput,
                    onValueChange = { viewModel.onScoreInputChange(it) },
                    label = { Text("Puan Miktarı") },
                    placeholder = { Text("Örn: 5.5 veya 100") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.inputError != null,
                    supportingText = {
                        uiState.inputError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal // ✅ Number -> Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFF9FA8DA)
                    )
                )

                // ✅ Reason Input
                OutlinedTextField(
                    value = uiState.reasonInput,
                    onValueChange = { viewModel.onReasonInputChange(it) },
                    label = { Text("Açıklama") },
                    placeholder = { Text("Örn: Proje tamamlama bonusu") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.reasonError != null,
                    supportingText = {
                        uiState.reasonError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFF9FA8DA)
                    ),
                    maxLines = 2
                )

                // Preview
                if (uiState.scoreInput.isNotEmpty()) {
                    val previewScore = uiState.scoreInput.toDoubleOrNull() // ✅ toIntOrNull -> toDoubleOrNull
                    if (previewScore != null && previewScore > 0) {
                        val change = if (uiState.isAdding) previewScore else -previewScore
                        val newScore = currentScore + change

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Yeni Puan:",
                                    fontSize = 14.sp,
                                    color = Color(0xFF1976D2)
                                )
                                Text(
                                    text = "$currentScore → $newScore",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isAdding) Color(0xFF4CAF50) else Color(0xFFFF5252)
                                )
                            }
                        }
                    }
                }

                // Error message
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = { viewModel.adjustScore(userId) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Kaydet")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(2.dp, color)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else color
            )
        }
    }
}
