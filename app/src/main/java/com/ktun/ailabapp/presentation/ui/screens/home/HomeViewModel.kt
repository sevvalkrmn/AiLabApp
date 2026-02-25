package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.LabStatsRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: ProfileResponse? = null,
    val userName: String = "",
    val greeting: String = "Günaydın",
    val currentTasks: List<TaskResponse> = emptyList(),

    // Lab Stats
    val currentOccupancy: Int = 0,
    val totalCapacity: Int = 16,
    val lastEntryDate: String? = null,
    val teammatesInside: Int = 0,
    val totalTeammates: Int = 0,

    val topUsers: List<TopUserItem> = emptyList(),

    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val selectedTask: TaskResponse? = null,
    val isTaskDetailLoading: Boolean = false
)

data class TopUserItem(
    val name: String,
    val score: Double,
    val avatarUrl: String?
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val labStatsRepository: LabStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadAllDataInternal()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
        updateGreeting()
    }

    fun refreshUserData() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadAllDataInternal()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    // Tüm yüklemeleri paralel çalıştırır ve hepsi bitince döner
    private suspend fun loadAllDataInternal() = coroutineScope {
        launch { loadUserDataInternal() }
        launch { loadCurrentTasksInternal() }
        launch { loadLabStatsInternal() }
        launch { loadLeaderboardInternal() }
    }

    fun loadTaskDetail(taskId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTaskDetailLoading = true)

            when (val result = taskRepository.getTaskDetail(taskId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isTaskDetailLoading = false,
                        selectedTask = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isTaskDetailLoading = false,
                        errorMessage = "Görev detayı alınamadı: ${result.message}"
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearSelectedTask() {
        _uiState.value = _uiState.value.copy(selectedTask = null)
    }

    private suspend fun loadUserDataInternal() {
        when (val result = authRepository.getProfile()) {
            is NetworkResult.Success -> {
                result.data?.let { profile ->
                    val firstName = profile.fullName.split(" ").firstOrNull() ?: "Kullanıcı"
                    _uiState.value = _uiState.value.copy(
                        user = profile,
                        userName = firstName
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e("Profile error: ${result.message}", tag = "HomeViewModel")
                _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    private suspend fun loadLeaderboardInternal() {
        var backoffMs = 500L

        repeat(3) { attempt ->
            when (val result = authRepository.getLeaderboard()) {
                is NetworkResult.Success -> {
                    result.data?.let { leaderboard ->
                        val topUsers = leaderboard.take(3).map { user ->
                            TopUserItem(
                                name = user.fullName,
                                score = user.totalScore,
                                avatarUrl = user.profileImageUrl
                            )
                        }
                        _uiState.value = _uiState.value.copy(topUsers = topUsers)
                        return
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("Leaderboard hatasi (deneme ${attempt + 1}): ${result.message}", tag = "HomeViewModel")
                    if (attempt < 2) {
                        delay(backoffMs)
                        backoffMs *= 2
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }

        Logger.e("Leaderboard 3 denemede de yuklenemedi", tag = "HomeViewModel")
    }

    private suspend fun loadCurrentTasksInternal() {
        when (val result = taskRepository.getMyTasks()) {
            is NetworkResult.Success -> {
                result.data?.let { allTasks ->
                    val activeTasks = allTasks.filter { it.status != "Done" }
                    _uiState.value = _uiState.value.copy(currentTasks = activeTasks)
                }
            }
            is NetworkResult.Error -> {
                Logger.e("Tasks error: ${result.message}", tag = "HomeViewModel")
            }
            is NetworkResult.Loading -> {}
        }
    }

    private suspend fun loadLabStatsInternal() {
        when (val result = labStatsRepository.getGlobalLabStats()) {
            is NetworkResult.Success -> {
                result.data?.let { stats ->
                    _uiState.value = _uiState.value.copy(
                        currentOccupancy = stats.currentOccupancyCount,
                        totalCapacity = 16
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e("Global Lab Stats hatasi: ${result.message}", tag = "HomeViewModel")
            }
            is NetworkResult.Loading -> {}
        }

        when (val result = labStatsRepository.getTeammatesStats()) {
            is NetworkResult.Success -> {
                result.data?.let { stats ->
                    _uiState.value = _uiState.value.copy(
                        teammatesInside = stats.teammatesInsideCount,
                        totalTeammates = stats.totalTeammatesCount
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e("Teammates Stats hatasi: ${result.message}", tag = "HomeViewModel")
            }
            is NetworkResult.Loading -> {}
        }

        when (val result = labStatsRepository.getPersonalLabStats()) {
            is NetworkResult.Success -> {
                result.data?.let { stats ->
                    _uiState.value = _uiState.value.copy(lastEntryDate = stats.lastEntryDate)
                }
            }
            is NetworkResult.Error -> {
                Logger.e("Personal Stats hatasi: ${result.message}", tag = "HomeViewModel")
            }
            is NetworkResult.Loading -> {}
        }
    }

    private fun updateGreeting() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 6..11 -> "Günaydın"
            in 12..17 -> "İyi öğlenler"
            in 18..21 -> "İyi akşamlar"
            else -> "İyi geceler"
        }
        _uiState.value = _uiState.value.copy(greeting = greeting)
    }
}
