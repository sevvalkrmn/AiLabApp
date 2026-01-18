// screens/admin/users/UsersListScreen.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSendAnnouncement: (String, String) -> Unit,
    onNavigateToManageRoles: (String) -> Unit,
    viewModel: UsersListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isLoadingDetail by remember { mutableStateOf(false) }

    // Navigation event listener
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AdminNavigationEvent.ToSendAnnouncement -> {
                    onNavigateToSendAnnouncement(event.userId, event.userName)
                }
                is AdminNavigationEvent.ToManageRoles -> {
                    onNavigateToManageRoles(event.userId)
                }
            }
        }
    }

    // ✅ YENİ - ManageRoles'tan döndüğünde user'ı refresh et
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                // ManageRoles'tan döndüyse ve bir user seçiliyse, refresh et
                selectedUser?.let { user ->
                    android.util.Log.d("UsersListScreen", "🔄 Refreshing user: ${user.id}")
                    isLoadingDetail = true
                    viewModel.loadUserDetail(user.id) { updatedUser ->
                        isLoadingDetail = false
                        selectedUser = updatedUser
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E)
                )
            )
        },
        containerColor = Color(0xFF1A237E)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A237E))
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Tüm Kullanıcılar",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tüm kullanıcıları sayfalı bir şekilde listeler",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = Color(0xFFF5F5FF)
                ) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF1A237E))
                            }
                        }

                        uiState.errorMessage != null -> {
                            ErrorState(
                                message = uiState.errorMessage!!,
                                onRetry = viewModel::loadUsers
                            )
                        }

                        uiState.filteredUsers.isEmpty() -> {
                            EmptyState(searchQuery = uiState.searchQuery)
                        }

                        else -> {
                            UsersList(
                                users = uiState.filteredUsers,
                                onUserClick = { user ->
                                    android.util.Log.d("UsersListScreen", "👤 Clicked: ${user.fullName} (${user.id})")

                                    isLoadingDetail = true
                                    viewModel.loadUserDetail(user.id) { detailedUser ->
                                        isLoadingDetail = false
                                        selectedUser = detailedUser
                                        showBottomSheet = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (isLoadingDetail) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    if (showBottomSheet && selectedUser != null) {
        UserDetailBottomSheet(
            user = selectedUser!!,
            onDismiss = {
                showBottomSheet = false
                // ✅ Bottom sheet kapanınca user'ı null yapma - refresh için gerekli
                // selectedUser = null // ← BUNU KALDIRDIM
            },
            onEditClick = {
                android.util.Log.d("UsersListScreen", "✏️ Edit: ${selectedUser?.id}")
            },
            onSendAnnouncement = { userId, userName ->
                viewModel.onSendAnnouncementClick(userId, userName)
                showBottomSheet = false
                selectedUser = null
            },
            onManageRoles = { userId ->
                viewModel.onManageRolesClick(userId)
                showBottomSheet = false
                // ✅ selectedUser'ı null YAPMA - refresh için gerekli
                // selectedUser = null // ← BUNU KALDIRDIM
            }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                "Search",
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Ara",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        singleLine = true
    )
}

@Composable
private fun UsersList(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = users,
            key = { it.id }
        ) { user ->
            UserListItem(
                user = user,
                onClick = { onUserClick(user) }
            )
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = user.profileImageUrl ?: "https://ui-avatars.com/api/?name=${user.fullName}&background=1A237E&color=fff",
                contentDescription = "Profil resmi",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A237E)
                )

                user.email.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            // Chevron Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detay",
                tint = Color(0xFF1A237E)
            )
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (searchQuery.isBlank()) {
                    "Henüz kullanıcı yok"
                } else {
                    "\"$searchQuery\" için sonuç bulunamadı"
                },
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bir hata oluştu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E)
                )
            ) {
                Text("Tekrar Dene")
            }
        }
    }
}