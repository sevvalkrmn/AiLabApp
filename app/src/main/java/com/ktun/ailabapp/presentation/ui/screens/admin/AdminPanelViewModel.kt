package com.ktun.ailabapp.presentation.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.LabStatsRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPanelUiState(
    val accessMode: Int = 0, // 0: Admin Only, 1: All Members
    val roomId: String = "313b8f7a-ff7e-4fd4-bb83-e4dda21e5b7e", // ✅ Sabit Oda ID
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val labStatsRepository: LabStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()

    init {
        // ✅ Doğrudan oda modunu yükle
        loadAccessMode(_uiState.value.roomId)
    }

    private fun loadAccessMode(roomId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = labStatsRepository.getAccessMode(roomId)) {
                is NetworkResult.Success -> {
                    result.data?.let { mode ->
                        _uiState.update { 
                            it.copy(
                                accessMode = mode,
                                isLoading = false 
                            ) 
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Erişim modu alınamadı: ${result.message}" 
                        ) 
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun toggleAccessMode() {
        val roomId = _uiState.value.roomId
        val currentMode = _uiState.value.accessMode
        val newMode = if (currentMode == 0) 1 else 0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = labStatsRepository.updateAccessMode(roomId, newMode)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            accessMode = newPasswordMode(newMode), // Local UI update
                            isLoading = false 
                        ) 
                    }
                    // Sunucudan son durumu tekrar teyit et
                    loadAccessMode(roomId)
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message 
                        ) 
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun newPasswordMode(mode: Int): Int = mode // Helper for readability
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}