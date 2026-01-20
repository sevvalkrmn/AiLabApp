// presentation/ui/screens/admin/createproject/CreateProjectScreen.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.createproject

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Proje Oluştur") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Proje Adı
            OutlinedTextField(
                value = state.projectName,
                onValueChange = { viewModel.onProjectNameChange(it) },
                label = { Text("Proje Adı *") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Açıklama
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Açıklama (Opsiyonel)") },
                supportingText = { Text("${state.description.length}/1000") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Captain Dropdown
            Text(
                "Proje Kaptanı *",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = state.dropdownExpanded,
                onExpandedChange = { viewModel.toggleDropdown() }
            ) {
                OutlinedTextField(
                    value = state.selectedCaptain?.fullName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kaptan Seç") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.dropdownExpanded)
                    },
                    isError = state.captainError != null,
                    supportingText = state.captainError?.let {
                        { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = state.dropdownExpanded,
                    onDismissRequest = { viewModel.toggleDropdown() }
                ) {
                    if (state.availableUsers.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Kullanıcı bulunamadı") },
                            onClick = {},
                            enabled = false
                        )
                    } else {
                        state.availableUsers.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(user.fullName, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            user.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.selectCaptain(user)
                                    viewModel.toggleDropdown()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Oluştur Butonu
            Button(
                onClick = { viewModel.createProject() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Proje Oluştur")
                }
            }
        }
    }

    // Error Snackbar
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // Success Navigation
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Proje başarıyla oluşturuldu")
            onNavigateBack()
        }
    }
}