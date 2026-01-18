// presentation/ui/screens/admin/score/AdjustScoreViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.api.AdminScoreApi
import com.ktun.ailabapp.data.remote.dto.request.AdjustScoreRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdjustScoreUiState(
    val scoreInput: String = "",
    val reasonInput: String = "", // ✅ EKLE
    val isAdding: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val inputError: String? = null,
    val reasonError: String? = null // ✅ EKLE
)

@HiltViewModel
class AdjustScoreViewModel @Inject constructor(
    private val adminScoreApi: AdminScoreApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdjustScoreUiState())
    val uiState: StateFlow<AdjustScoreUiState> = _uiState.asStateFlow()

    fun onScoreInputChange(input: String) {
        if (input.isEmpty() || input.matches(Regex("^\\d+$"))) { // ✅ Sadece integer
            _uiState.update {
                it.copy(
                    scoreInput = input,
                    inputError = null
                )
            }
        }
    }

    // ✅ YENİ
    fun onReasonInputChange(input: String) {
        _uiState.update {
            it.copy(
                reasonInput = input,
                reasonError = null
            )
        }
    }

    fun toggleAddSubtract() {
        _uiState.update {
            it.copy(isAdding = !it.isAdding)
        }
    }

    fun adjustScore(userId: String) {
        val scoreInput = _uiState.value.scoreInput
        val reasonInput = _uiState.value.reasonInput.trim()

        // Validation
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

        val scoreValue = scoreInput.toIntOrNull()
        if (scoreValue == null || scoreValue <= 0) {
            _uiState.update { it.copy(inputError = "Geçerli bir puan giriniz") }
            return
        }

        // ✅ Pozitif/Negatif değer
        val finalScore = if (_uiState.value.isAdding) scoreValue else -scoreValue

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = adminScoreApi.adjustUserScore(
                    userId = userId,
                    request = AdjustScoreRequest(
                        amount = finalScore,
                        reason = reasonInput
                    )
                )

                if (response.isSuccessful) {
                    android.util.Log.d("AdjustScoreVM", "✅ Score adjusted: $finalScore, reason: $reasonInput")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AdjustScoreVM", "❌ Error ${response.code()}: $errorBody")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Puan güncellenemedi: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdjustScoreVM", "❌ Exception: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Hata: ${e.message}"
                    )
                }
            }
        }
    }
}