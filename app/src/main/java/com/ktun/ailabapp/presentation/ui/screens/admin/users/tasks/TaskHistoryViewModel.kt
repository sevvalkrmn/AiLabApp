package com.ktun.ailabapp.presentation.ui.screens.admin.users.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.data.remote.dto.response.TaskStatus
import com.ktun.ailabapp.domain.usecase.task.GetUserTaskHistoryUseCase
import com.ktun.ailabapp.util.NetworkResult
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
    private val getUserTaskHistoryUseCase: GetUserTaskHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskHistoryUiState())
    val uiState: StateFlow<TaskHistoryUiState> = _uiState.asStateFlow()

    fun loadTaskHistory(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getUserTaskHistoryUseCase(userId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(tasks = result.data ?: emptyList(), isLoading = false) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun setFilter(filter: TaskStatus?) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredTasks(): List<TaskHistory> {
        val filter = _uiState.value.selectedFilter
        val tasks = _uiState.value.tasks
        return if (filter == null) tasks else tasks.filter { it.status == filter }
    }
}
