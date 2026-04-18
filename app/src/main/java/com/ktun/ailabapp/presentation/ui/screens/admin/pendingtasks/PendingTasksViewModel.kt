package com.ktun.ailabapp.presentation.ui.screens.admin.pendingtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import com.ktun.ailabapp.domain.usecase.admin.score.AssignScoreUseCase
import com.ktun.ailabapp.domain.usecase.admin.score.GetPendingTasksUseCase
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PendingTasksUiState(
    val tasks: List<PendingTaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class PendingTasksViewModel @Inject constructor(
    private val getPendingTasksUseCase: GetPendingTasksUseCase,
    private val assignScoreUseCase: AssignScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendingTasksUiState())
    val uiState: StateFlow<PendingTasksUiState> = _uiState.asStateFlow()

    init {
        loadPendingTasks()
    }

    fun loadPendingTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getPendingTasksUseCase()) {
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

    fun assignScore(taskId: String, scoreCategory: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            when (val result = assignScoreUseCase(taskId, scoreCategory)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Puan başarıyla atandı") }
                    loadPendingTasks()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }
}
