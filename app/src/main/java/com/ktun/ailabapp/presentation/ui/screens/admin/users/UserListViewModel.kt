package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.repository.RfidRepository
import com.ktun.ailabapp.domain.usecase.user.DeleteUserUseCase
import com.ktun.ailabapp.domain.usecase.user.GetAllUsersUseCase
import com.ktun.ailabapp.domain.usecase.user.GetUserByIdUseCase
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val searchQuery: String = "",
    val rfidRegistering: Boolean = false
)

sealed interface AdminNavigationEvent {
    data class ToSendAnnouncement(val userId: String, val userName: String) : AdminNavigationEvent
    data class ToManageRoles(val userId: String) : AdminNavigationEvent
    data class ToTaskHistory(val userId: String, val userName: String) : AdminNavigationEvent
}

sealed interface RfidEvent {
    data class RegistrationComplete(val userName: String) : RfidEvent
    data class RegistrationFailed(val message: String) : RfidEvent
    data object RegistrationTimeout : RfidEvent
}

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val rfidRepository: RfidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersListUiState())
    val uiState: StateFlow<UsersListUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AdminNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _rfidEvent = MutableSharedFlow<RfidEvent>()
    val rfidEvent = _rfidEvent.asSharedFlow()

    private var rfidPollingJob: Job? = null

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getAllUsersUseCase()) {
                is NetworkResult.Success -> {
                    val users = result.data ?: emptyList()
                    _uiState.update { it.copy(users = users, filteredUsers = users, isLoading = false) }
                    Logger.d("✅ ${users.size} kullanıcı yüklendi")
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    Logger.e("❌ Hata: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun loadUserDetail(userId: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            Logger.d("🔍 Loading detail for userId: $userId")

            when (val result = getUserByIdUseCase(userId)) {
                is NetworkResult.Success -> {
                    result.data?.let { user ->
                        Logger.d("✅ User loaded: ${user.fullName}")
                        onSuccess(user)
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("❌ Error: ${result.message}")
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
            currentState.copy(searchQuery = query, filteredUsers = filtered)
        }
    }

    fun onSendAnnouncementClick(userId: String, userName: String) {
        viewModelScope.launch {
            _navigationEvent.emit(AdminNavigationEvent.ToSendAnnouncement(userId, userName))
        }
    }

    fun onManageRolesClick(userId: String) {
        viewModelScope.launch {
            _navigationEvent.emit(AdminNavigationEvent.ToManageRoles(userId))
        }
    }

    fun onTaskHistoryClick(userId: String, userName: String) {
        viewModelScope.launch {
            _navigationEvent.emit(AdminNavigationEvent.ToTaskHistory(userId, userName))
        }
    }

    fun startRfidRegistration(userId: String, userName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = rfidRepository.startRegistration(userId)) {
                is NetworkResult.Success -> {
                    Logger.d("✅ RFID Kayıt Modu Başlatıldı: $userId")
                    _uiState.update { it.copy(rfidRegistering = true) }
                    onSuccess()
                    startRfidPolling(userName)
                }
                is NetworkResult.Error -> {
                    Logger.e("❌ RFID Hata: ${result.message}")
                    onError(result.message ?: "Bilinmeyen hata")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun startRfidPolling(userName: String) {
        rfidPollingJob?.cancel()
        rfidPollingJob = viewModelScope.launch {
            val timeoutMs = 60_000L
            val intervalMs = 1_500L
            var elapsed = 0L

            while (elapsed < timeoutMs) {
                delay(intervalMs)
                elapsed += intervalMs

                when (val result = rfidRepository.checkStatus()) {
                    is NetworkResult.Success -> {
                        val mode = result.data
                        if (mode != "register") {
                            Logger.d("✅ RFID kart kaydı tamamlandı - $userName")
                            _uiState.update { it.copy(rfidRegistering = false) }
                            _rfidEvent.emit(RfidEvent.RegistrationComplete(userName))
                            return@launch
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("RFID status kontrol hatası: ${result.message}")
                    }
                    is NetworkResult.Loading -> {}
                }
            }

            Logger.d("RFID kayıt zaman aşımına uğradı")
            _uiState.update { it.copy(rfidRegistering = false) }
            _rfidEvent.emit(RfidEvent.RegistrationTimeout)
        }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = deleteUserUseCase(userId)) {
                is NetworkResult.Success -> {
                    Logger.d("✅ Kullanıcı silindi: $userId")
                    loadUsers()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    Logger.e("❌ Silme hatası: ${result.message}")
                    onError(result.message ?: "Silme işlemi başarısız")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
