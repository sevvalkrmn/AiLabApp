package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.ui.theme.InfoBlue
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.SuccessGreen
import com.ktun.ailabapp.ui.theme.WarningOrange
import com.ktun.ailabapp.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TaskDetailDialog(
    task: TaskResponse,
    onDismiss: () -> Unit
) {
    AnimatedDialog(onDismiss = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Baslik
                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                // Aciklama
                if (!task.description.isNullOrEmpty()) {
                    Column {
                        Text(
                            text = "Aciklama",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                } else {
                    Text(
                        text = "Aciklama yok",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                }

                HorizontalDivider()

                // Durum
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Durum",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                    Surface(
                        color = getStatusColor(task.status),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = getStatusText(task.status),
                            color = White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Son Tarih
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Son Tarih",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = formatDate(task.dueDate),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                // Puan
                if (task.score != null && task.score > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Puan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "${task.score} Puan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }

                // Kapat butonu
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Kapat")
                }
            }
        }
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "Todo" -> WarningOrange
        "InProgress" -> InfoBlue
        "Done" -> SuccessGreen
        else -> Color.Gray
    }
}

fun getStatusText(status: String): String {
    return when (status) {
        "Todo" -> "Yapilacak"
        "InProgress" -> "Devam Ediyor"
        "Done" -> "Tamamlandi"
        else -> status
    }
}

private fun formatDate(isoDate: String?): String {
    if (isoDate.isNullOrEmpty()) return "Tarih Yok"
    return try {
        val parserMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parserMillis.timeZone = TimeZone.getTimeZone("UTC")

        val parserNoMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        parserNoMillis.timeZone = TimeZone.getTimeZone("UTC")

        val parserSimple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = try {
            parserMillis.parse(isoDate)
        } catch (e: Exception) {
            try {
                parserNoMillis.parse(isoDate)
            } catch (e2: Exception) {
                parserSimple.parse(isoDate)
            }
        }

        if (date != null) {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        } else {
            isoDate
        }
    } catch (e: Exception) {
        isoDate
    }
}
