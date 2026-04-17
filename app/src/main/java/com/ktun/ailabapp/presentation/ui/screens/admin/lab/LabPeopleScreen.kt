package com.ktun.ailabapp.presentation.ui.screens.admin.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ktun.ailabapp.ui.theme.*
import com.ktun.ailabapp.presentation.ui.components.navigation.AiLabTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabPeopleScreen(
    onNavigateBack: () -> Unit,
    viewModel: LabPeopleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AiLabTopBar(title = "Lab'daki Kişiler", onBackClick = onNavigateBack)
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadData()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading && !isRefreshing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hata Oluştu",
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = uiState.errorMessage ?: "")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadData() }) {
                                Text("Tekrar Dene")
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Occupancy Header
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = White,
                            shadowElevation = AppDimensions.cardElevation
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Doluluk Oranı",
                                        fontSize = 14.sp,
                                        color = TextGray
                                    )
                                    Text(
                                        text = "${uiState.currentOccupancy} / ${uiState.totalCapacity}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }

                                CircularProgressIndicator(
                                    progress = { if (uiState.totalCapacity > 0) uiState.currentOccupancy.toFloat() / uiState.totalCapacity else 0f },
                                    modifier = Modifier.size(50.dp),
                                    color = PrimaryBlue,
                                    trackColor = PrimaryBlue.copy(alpha = 0.2f)
                                )
                            }
                        }

                        if (uiState.peopleInside.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Şu an laboratuvarda kimse yok.")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "İçerideki Kişiler (${uiState.peopleInside.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                items(uiState.peopleInside.size) { index ->
                                    PersonItem(
                                        person = uiState.peopleInside[index],
                                        onCheckoutClick = {
                                            uiState.peopleInside[index].id?.let { viewModel.forceCheckout(it) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        } // Column
    }
}

@Composable
fun PersonItem(
    person: LabPerson,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = person.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Çıkış Butonu
            IconButton(
                onClick = onCheckoutClick,
                enabled = person.id != null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Zorla Çıkar",
                    tint = if (person.id != null) ErrorRed else TextGray
                )
            }
        }
    }
}