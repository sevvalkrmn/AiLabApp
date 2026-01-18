package com.ktun.ailabapp.presentation.ui.screens.admin.users.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.api.TaskApi
import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.data.remote.dto.response.TaskStatus
import com.ktun.ailabapp.data.remote.dto.response.toTaskHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskHistoryUiState(
    val tasks: List<TaskHistory> = emptyList(),
    val selectedFilter: TaskStatus? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TaskHistoryViewModel @Inject constructor(
    private val taskApi: TaskApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskHistoryUiState())
    val uiState: StateFlow<TaskHistoryUiState> = _uiState.asStateFlow()

    fun loadTaskHistory(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = taskApi.getUserTaskHistory(userId)

                if (response.isSuccessful) {
                    val tasks = response.body()?.map { it.toTaskHistory() } ?: emptyList()
                    android.util.Log.d("TaskHistoryVM", "✅ Loaded ${tasks.size} tasks")

                    _uiState.update {
                        it.copy(
                            tasks = tasks,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Görevler yüklenemedi: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskHistoryVM", "❌ Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Hata: ${e.message}"
                    )
                }
            }
        }
    }

    fun setFilter(filter: TaskStatus?) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredTasks(): List<TaskHistory> {
        val filter = _uiState.value.selectedFilter
        val tasks = _uiState.value.tasks

        return if (filter == null) {
            tasks
        } else {
            tasks.filter { it.status == filter }
        }
    }
}