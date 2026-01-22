package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    pageInfo: String = "unknown-page", // Hatanın olduğu sayfa
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit = {}, // Eski parametre uyumluluğu için, artık kullanılmayabilir
    viewModel: BugReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var description by remember { mutableStateOf("") }
    var selectedBugType by remember { mutableStateOf(1) } // Default: Görsel (1)
    var expanded by remember { mutableStateOf(false) }

    val bugTypes = listOf(
        1 to "Görsel Sorun",
        2 to "Fonksiyonel Hata",
        3 to "Performans Sorunu",
        4 to "Uygulama Çökmesi",
        5 to "Yetki Sorunu",
        99 to "Diğer"
    )

    // Başarılı olursa kapat
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onDismiss()
            viewModel.resetState()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                            imageVector = Icons.Default.BugReport,
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

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF071372))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hata Tipi Seçimi
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = bugTypes.find { it.first == selectedBugType }?.second ?: "Seçiniz",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hata Türü") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF071372),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        bugTypes.forEach { (id, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedBugType = id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Açıklama
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text("Açıklama") },
                    placeholder = { Text("Lütfen hatayı detaylıca açıklayın...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF071372),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6
                )

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Butonlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal", color = Color(0xFF071372))
                    }

                    Button(
                        onClick = {
                            viewModel.sendBugReport(
                                bugType = selectedBugType,
                                pageInfo = pageInfo,
                                description = description,
                                onSuccess = {
                                    // Toast veya başka bir işlem yapılabilir
                                }
                            )
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF071372)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = description.isNotBlank() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Gönder")
                        }
                    }
                }
            }
        }
    }
}
