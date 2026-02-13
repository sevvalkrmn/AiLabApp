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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: ProfileResponse? = null,
    val userName: String = "",
    val greeting: String = "Good Morning",
    val currentTasks: List<TaskResponse> = emptyList(),

    // Lab Stats
    val currentOccupancy: Int = 0,
    val totalCapacity: Int = 16, // ✅ Başlangıç değeri statik 16
    val lastEntryDate: String? = null,
    val teammatesInside: Int = 0,
    val totalTeammates: Int = 0,

    val topUsers: List<TopUserItem> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // ✅ YENİ: Seçili Görev Detayı
    val selectedTask: TaskResponse? = null,
    val isTaskDetailLoading: Boolean = false
)

data class TopUserItem(
    val name: String,
    val score: Double, // ✅ Int -> Double
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

    private var pollingJob: Job? = null

    companion object {
        private const val POLLING_INTERVAL_MS = 30_000L
    }

    init {
        loadAllData()
        updateGreeting()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            launch { loadUserData() }
            launch { loadCurrentTasks() }
            launch { loadLabStats() }
            launch { loadLeaderboard() }
        }
    }

    fun refreshUserData() {
        loadAllData()
    }

    /** Ekran görünür olduğunda çağrılır (ON_RESUME) */
    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(POLLING_INTERVAL_MS)
                Logger.d("Otomatik lab stats guncelleniyor...", tag = "HomeViewModel")
                loadLabStats()
            }
        }
    }

    /** Ekran arka plana geçtiğinde çağrılır (ON_PAUSE) */
    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    // ✅ YENİ: Görev Detayını Çek
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

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    result.data?.let { profile ->
                        val firstName = profile.fullName.split(" ").firstOrNull() ?: "Kullanıcı"
                        _uiState.value = _uiState.value.copy(
                            user = profile,
                            userName = firstName,
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("Profile error: ${result.message}", tag = "HomeViewModel")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
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
                            return@launch
                        }
                    }
                    is NetworkResult.Error -> {
                        Logger.e("Leaderboard hatasi (deneme ${attempt + 1}): ${result.message}", tag = "HomeViewModel")
                        if (attempt < 2) {
                            delay(backoffMs)
                            backoffMs *= 2 // Exponential backoff: 500 -> 1000 -> 2000
                        }
                    }
                    is NetworkResult.Loading -> {}
                }
            }

            Logger.e("Leaderboard 3 denemede de yuklenemedi", tag = "HomeViewModel")
        }
    }

    private fun loadCurrentTasks() {
        viewModelScope.launch {
            when (val result = taskRepository.getMyTasks()) {
                is NetworkResult.Success -> {
                    result.data?.let { allTasks ->
                        val activeTasks = allTasks.filter { it.status != "Done" }
                        _uiState.value = _uiState.value.copy(
                            currentTasks = activeTasks
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("Tasks error: ${result.message}", tag = "HomeViewModel")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadLabStats() {
        viewModelScope.launch {
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
                        _uiState.value = _uiState.value.copy(
                            lastEntryDate = stats.lastEntryDate
                        )
                    }
                }
                is NetworkResult.Error -> {
                    Logger.e("Personal Stats hatasi: ${result.message}", tag = "HomeViewModel")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun updateGreeting() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 6..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
        _uiState.value = _uiState.value.copy(greeting = greeting)
    }
}