package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.LabStatsRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: ProfileResponse? = null,
    val userName: String = "",
    val greeting: String = "Good Morning",
    val currentTasks: List<TaskResponse> = emptyList(),

    // Lab Stats
    val currentOccupancy: Int = 0,
    val totalCapacity: Int = 0,
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

    init {
        loadAllData()
        updateGreeting()
        startPeriodicLabStatsUpdate()
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

    private fun startPeriodicLabStatsUpdate() {
        viewModelScope.launch {
            while (true) {
                delay(10_000)
                android.util.Log.d("HomeViewModel", "🔄 Otomatik lab stats güncelleniyor...")
                loadLabStats()
            }
        }
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

            android.util.Log.d("HomeViewModel", "🔵 loadUserData() başladı")

            when (val result = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    android.util.Log.d("HomeViewModel", "✅ Profile loaded successfully")
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
                    android.util.Log.e("HomeViewModel", "❌ Profile error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // ✅ TEK loadLeaderboard fonksiyonu - Retry mekanizmalı versiyon
    private fun loadLeaderboard() {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "🏆 Leaderboard yükleniyor...")

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

                            android.util.Log.d("HomeViewModel", """
                                ✅ Leaderboard yüklendi (deneme ${attempt + 1}):
                                - 1. ${topUsers.getOrNull(0)?.name} (${topUsers.getOrNull(0)?.score})
                                - 2. ${topUsers.getOrNull(1)?.name} (${topUsers.getOrNull(1)?.score})
                                - 3. ${topUsers.getOrNull(2)?.name} (${topUsers.getOrNull(2)?.score})
                            """.trimIndent())

                            _uiState.value = _uiState.value.copy(topUsers = topUsers)
                            return@launch
                        }
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.e("HomeViewModel", "❌ Leaderboard hatası (deneme ${attempt + 1}): ${result.message}")

                        if (attempt < 2) {
                            delay(500)
                        }
                    }
                    is NetworkResult.Loading -> {}
                }
            }

            android.util.Log.e("HomeViewModel", "❌ Leaderboard 3 denemede de yüklenemedi")
        }
    }

    private fun loadCurrentTasks() {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "🔵 loadCurrentTasks() başladı")

            when (val result = taskRepository.getMyTasks()) {
                is NetworkResult.Success -> {
                    android.util.Log.d("HomeViewModel", "✅ NetworkResult.Success")

                    result.data?.let { allTasks ->
                        android.util.Log.d("HomeViewModel", "📦 Gelen görev sayısı: ${allTasks.size}")

                        allTasks.forEachIndexed { index, task ->
                            android.util.Log.d("HomeViewModel", """
                            ----------------------------------------
                            Görev #$index:
                            - ID: ${task.id}
                            - Title: ${task.title}
                            - Status String: '${task.status}'
                            - Raw _status: ${task._status}
                            - Description: ${task.description}
                            - Project: ${task.projectName}
                            ----------------------------------------
                        """.trimIndent())
                        }

                        // ✅ GÜNCELLEME: Sadece tamamlanmamış görevleri al ve sınır koyma
                        val activeTasks = allTasks.filter { it.status != "Done" }

                        android.util.Log.d("HomeViewModel", "🎯 UI'a gönderilen aktif görev sayısı: ${activeTasks.size}")

                        _uiState.value = _uiState.value.copy(
                            currentTasks = activeTasks
                        )

                        android.util.Log.d("HomeViewModel", "✅ uiState güncellendi - currentTasks.size: ${_uiState.value.currentTasks.size}")

                    } ?: run {
                        android.util.Log.e("HomeViewModel", "❌ result.data NULL!")
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "❌ NetworkResult.Error: ${result.message}")
                }
                is NetworkResult.Loading -> {
                    android.util.Log.d("HomeViewModel", "⏳ NetworkResult.Loading")
                }
            }
        }
    }

    private fun loadLabStats() {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "🔵 loadLabStats() başladı")

            when (val result = labStatsRepository.getGlobalLabStats()) {
                is NetworkResult.Success -> {
                    result.data?.let { stats ->
                        android.util.Log.d("HomeViewModel", """
                        ✅ Global Lab Stats yüklendi:
                        - Doluluk: ${stats.currentOccupancyCount}/${stats.totalCapacity}
                        - İçerideki kişiler: ${stats.peopleInside.size} kişi
                    """.trimIndent())

                        _uiState.value = _uiState.value.copy(
                            currentOccupancy = stats.currentOccupancyCount,
                            totalCapacity = stats.totalCapacity
                        )
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "❌ Global Lab Stats hatası: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }

            when (val result = labStatsRepository.getTeammatesStats()) {
                is NetworkResult.Success -> {
                    result.data?.let { stats ->
                        android.util.Log.d("HomeViewModel", """
                        ✅ Teammates Stats yüklendi:
                        - Takım arkadaşları: ${stats.teammatesInsideCount}/${stats.totalTeammatesCount}
                    """.trimIndent())

                        _uiState.value = _uiState.value.copy(
                            teammatesInside = stats.teammatesInsideCount,
                            totalTeammates = stats.totalTeammatesCount
                        )
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "❌ Teammates Stats hatası: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }

            when (val result = labStatsRepository.getPersonalLabStats()) {
                is NetworkResult.Success -> {
                    result.data?.let { stats ->
                        android.util.Log.d("HomeViewModel", """
                        ✅ Personal Stats yüklendi:
                        - Son giriş: ${stats.lastEntryDate ?: "Hiç giriş yapılmamış"}
                        - Toplam süre: ${stats.totalTimeSpent}
                    """.trimIndent())

                        _uiState.value = _uiState.value.copy(
                            lastEntryDate = stats.lastEntryDate
                        )
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "❌ Personal Stats hatası: ${result.message}")
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