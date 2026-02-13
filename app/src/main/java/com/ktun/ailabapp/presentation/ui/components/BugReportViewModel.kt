package com.ktun.ailabapp.presentation.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.BugReportRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BugReportUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BugReportViewModel @Inject constructor(
    private val bugReportRepository: BugReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BugReportUiState())
    val uiState: StateFlow<BugReportUiState> = _uiState.asStateFlow()

    // Basit bir throttling (hız sınırı) mekanizması
    private var lastReportTime: Long = 0
    private val THROTTLE_MS = 30_000L // 30 saniye

    fun sendBugReport(
        bugType: Int,
        pageInfo: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastReportTime < THROTTLE_MS) {
            _uiState.update { it.copy(error = "Lütfen yeni bir bildirim göndermeden önce 30 saniye bekleyin.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            when (val result = bugReportRepository.createBugReport(bugType, pageInfo, description)) {
                is NetworkResult.Success -> {
                    lastReportTime = System.currentTimeMillis()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun resetState() {
        _uiState.value = BugReportUiState()
    }
}
