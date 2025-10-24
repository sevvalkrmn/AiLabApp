package com.ktunailab.ailabapp.presentation.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ktunailab.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktunailab.ailabapp.data.remote.dto.response.TaskResponse
import com.ktunailab.ailabapp.data.repository.AuthRepository
import com.ktunailab.ailabapp.data.repository.TaskRepository
import com.ktunailab.ailabapp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: ProfileResponse? = null,
    val userName: String = "",
    val greeting: String = "Good Morning",
    val currentTasks: List<TaskResponse> = emptyList(),  // ← Görevler eklendi
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)
    private val taskRepository = TaskRepository(application.applicationContext)  // ← Eklendi

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadCurrentTasks()  // ← Görevleri yükle
        updateGreeting()
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

                        android.util.Log.d("HomeViewModel", """
                            User loaded: $firstName
                            Total Score: ${profile.totalScore}
                            Avatar URL: ${profile.avatarUrl ?: "Yok"}
                        """.trimIndent())
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("HomeViewModel", "Profile error: ${result.message}")

                    if (result.message?.contains("Oturum süresi") == true) {
                        android.util.Log.d("HomeViewModel", "Token expired, yenileme deneniyor...")

                        when (val refreshResult = authRepository.refreshToken()) {
                            is NetworkResult.Success -> {
                                android.util.Log.d("HomeViewModel", "Token yenilendi, profil tekrar yükleniyor...")
                                loadUserData()
                            }
                            is NetworkResult.Error -> {
                                android.util.Log.e("HomeViewModel", "Token yenileme başarısız: ${refreshResult.message}")
                                _uiState.value = _uiState.value.copy(
                                    userName = "Kullanıcı",
                                    isLoading = false,
                                    errorMessage = "Lütfen tekrar giriş yapın"
                                )
                            }
                            is NetworkResult.Loading -> {}
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            userName = "Kullanıcı",
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
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

                        // HER GÖREVİ DETAYLI LOGLA
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

                        // FİLTRESİZ HEMEN ATAR - TEST İÇİN
                        val testTasks = allTasks.take(2)

                        android.util.Log.d("HomeViewModel", "🎯 UI'a gönderilen görev sayısı: ${testTasks.size}")

                        _uiState.value = _uiState.value.copy(
                            currentTasks = testTasks
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

    fun refreshUserData() {
        loadUserData()
        loadCurrentTasks()
    }
}