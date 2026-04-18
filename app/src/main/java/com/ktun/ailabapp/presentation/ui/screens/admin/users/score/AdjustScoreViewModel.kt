package com.ktun.ailabapp.presentation.ui.screens.admin.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.domain.usecase.admin.score.AdjustUserScoreUseCase
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdjustScoreUiState(
    val scoreInput: String = "",
    val reasonInput: String = "",
    val isAdding: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val reasonError: String? = null
)

@HiltViewModel
class AdjustScoreViewModel @Inject constructor(
    private val adjustUserScoreUseCase: AdjustUserScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdjustScoreUiState())
    val uiState: StateFlow<AdjustScoreUiState> = _uiState.asStateFlow()

    fun onScoreInputChange(input: String) {
        if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(scoreInput = input, inputError = null) }
        }
    }

    fun onReasonInputChange(input: String) {
        _uiState.update { it.copy(reasonInput = input, reasonError = null) }
    }

    fun toggleAddSubtract() {
        _uiState.update { it.copy(isAdding = !it.isAdding) }
    }

    fun resetState() {
        _uiState.value = AdjustScoreUiState()
    }

    fun adjustScore(userId: String) {
        val scoreInput = _uiState.value.scoreInput
        val reasonInput = _uiState.value.reasonInput.trim()

        var hasError = false
        if (scoreInput.isBlank()) {
            _uiState.update { it.copy(inputError = "Puan giriniz") }
            hasError = true
        }
        if (reasonInput.isBlank()) {
            _uiState.update { it.copy(reasonError = "Açıklama giriniz") }
            hasError = true
        }
        if (hasError) return

        val scoreValue = scoreInput.toDoubleOrNull()
        if (scoreValue == null || scoreValue <= 0) {
            _uiState.update { it.copy(inputError = "Geçerli bir puan giriniz") }
            return
        }

        val finalAmount = if (_uiState.value.isAdding) scoreValue else -scoreValue

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = adjustUserScoreUseCase(userId, finalAmount, reasonInput)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
