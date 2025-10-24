package com.ktunailab.ailabapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Hata Bildir",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF071372)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color(0xFF071372)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Açıklama
                Text(
                    text = "Karşılaştığınız hatayı detaylı bir şekilde açıklayın. Geri bildiriminiz bizim için çok değerli!",
                    fontSize = 14.sp,
                    color = Color(0xFF071372).copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Text Field
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = {
                        Text(
                            text = "Örnek: Projelerim sayfasında görevlere tıkladığımda uygulama donuyor...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF071372),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Butonlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // İptal Butonu
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF071372)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF071372)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSubmitting
                    ) {
                        Text(
                            "İptal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Gönder Butonu
                    Button(
                        onClick = {
                            if (feedbackText.isNotBlank()) {
                                isSubmitting = true
                                onSubmit(feedbackText)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF071372),
                            disabledContainerColor = Color(0xFF071372).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = feedbackText.isNotBlank() && !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Gönder",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}