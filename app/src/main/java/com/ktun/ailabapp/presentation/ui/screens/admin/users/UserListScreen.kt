// screens/admin/users/UsersListScreen.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.users

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.presentation.ui.screens.admin.users.DeleteUserDialog
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSendAnnouncement: (String, String) -> Unit,
    onNavigateToManageRoles: (String) -> Unit,
    onNavigateToTaskHistory: (String, String) -> Unit,
    onNavigateToProjectDetail: (String) -> Unit = {},
    viewModel: UsersListViewModel = hiltViewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isLoadingDetail by remember { mutableStateOf(false) }

    // ✅ YENİ STATE: Silme Dialogu
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

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
                is AdminNavigationEvent.ToTaskHistory -> {
                    onNavigateToTaskHistory(event.userId, event.userName)
                }
            }
        }
    }

    // ✅ YENİ - ManageRoles'tan döndüğünde user'ı refresh et
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
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
                    containerColor = PrimaryBlue
                )
            )
        },
        containerColor = PrimaryBlue
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
                        .background(PrimaryBlue)
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
                    color = BackgroundLight
                ) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
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
            },
            onViewTaskHistory = { userId, userName ->
                viewModel.onTaskHistoryClick(userId, userName)
                showBottomSheet = false
            },
            onImageUpdated = {
                // ✅ Fotoğraf güncellendiğinde hem detayı hem listeyi yenile
                selectedUser?.let { user ->
                    android.util.Log.d("UsersListScreen", "🔄 Refreshing user after image update: ${user.id}")
                    viewModel.loadUserDetail(user.id) { updatedUser ->
                        selectedUser = updatedUser
                    }
                    viewModel.loadUsers()
                }
            },
            onScoreUpdated = {
                // ✅ Puan güncellendiğinde hem detayı hem listeyi yenile
                selectedUser?.let { user ->
                    android.util.Log.d("UsersListScreen", "🔄 Refreshing user after score adjustment: ${user.id}")
                    viewModel.loadUserDetail(user.id) { updatedUser ->
                        selectedUser = updatedUser
                    }
                    viewModel.loadUsers()
                }
            },
            onProjectClick = { projectId ->
                showBottomSheet = false
                onNavigateToProjectDetail(projectId)
            },
            onRfidClick = { userId ->
                viewModel.startRfidRegistration(
                    userId = userId,
                    onSuccess = {
                        Toast.makeText(context, "RFID Kayıt Modu Başlatıldı! Kartı okutun.", Toast.LENGTH_LONG).show()
                    },
                    onError = { message ->
                        Toast.makeText(context, "Hata: $message", Toast.LENGTH_LONG).show()
                    }
                )
            },
            onDeleteClick = { userId -> // ✅ Silme Butonu Tıklandı
                userToDelete = selectedUser // O anki seçili kullanıcı
                showDeleteDialog = true
                showBottomSheet = false // Bottom sheet'i kapat
            }
        )
    }

    // ✅ YENİ: Silme Onay Dialogu
    if (showDeleteDialog && userToDelete != null) {
        DeleteUserDialog(
            userName = userToDelete!!.fullName,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteUser(
                    userId = userToDelete!!.id,
                    onSuccess = {
                        Toast.makeText(context, "Kullanıcı başarıyla silindi", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
)
{
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
)
{
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
        ) { user -> // ✅ Parametre eklendi
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
)
{
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
                    color = PrimaryBlue
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
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String
)
{
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
)
{
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
                    containerColor = PrimaryBlue
                )
            ) {
                Text("Tekrar Dene")
            }
        }
    }
}
