// presentation/ui/screens/admin/users/UsersListViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.repository.UserRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsersListUiState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

// ✅ YENİ - Sealed interface
sealed interface AdminNavigationEvent {
    data class ToSendAnnouncement(val userId: String, val userName: String) : AdminNavigationEvent
    data class ToManageRoles(val userId: String) : AdminNavigationEvent // ✅ YENİ
}

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersListUiState())
    val uiState: StateFlow<UsersListUiState> = _uiState.asStateFlow()

    // ✅ YENİ - Navigation event
    private val _navigationEvent = MutableSharedFlow<AdminNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = userRepository.getAllUsers()) {
                is NetworkResult.Success -> {
                    val users = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            users = users,
                            filteredUsers = users,
                            isLoading = false
                        )
                    }
                    android.util.Log.d("UsersListVM", "✅ ${users.size} kullanıcı yüklendi")
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    android.util.Log.e("UsersListVM", "❌ Hata: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun loadUserDetail(userId: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            android.util.Log.d("UsersListVM", "🔍 Loading detail for userId: $userId")

            when (val result = userRepository.getUserById(userId)) {
                is NetworkResult.Success -> {
                    result.data?.let { user ->
                        android.util.Log.d("UsersListVM", "✅ User loaded: ${user.fullName}")
                        onSuccess(user)
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("UsersListVM", "❌ Error: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.users
            } else {
                currentState.users.filter { user ->
                    user.fullName.contains(query, ignoreCase = true) ||
                            user.email.contains(query, ignoreCase = true) ||
                            (user.studentNumber?.contains(query, ignoreCase = true) == true)
                }
            }

            currentState.copy(
                searchQuery = query,
                filteredUsers = filtered
            )
        }
    }

    // ✅ YENİ FONKSIYON
    fun onSendAnnouncementClick(userId: String, userName: String) {
        viewModelScope.launch {
            _navigationEvent.emit(
                AdminNavigationEvent.ToSendAnnouncement(userId, userName)
            )
        }
    }

    fun onManageRolesClick(userId: String) {
        viewModelScope.launch {
            _navigationEvent.emit(
                AdminNavigationEvent.ToManageRoles(userId)
            )
        }
    }
}