// presentation/ui/screens/admin/createproject/CreateProjectViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.createproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.UserRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProjectState())
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            when (val result = userRepository.getAllUsers()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(availableUsers = result.data ?: emptyList()) }  // ✅
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(error = "Kullanıcılar yüklenemedi: ${result.message}") }
                }
                is NetworkResult.Loading -> { /* Ignore */ }
            }
        }
    }

    fun onProjectNameChange(name: String) {
        if (name.length <= 200) {
            _state.update {
                it.copy(
                    projectName = name,
                    nameError = null
                )
            }
        }
    }

    fun onDescriptionChange(desc: String) {
        if (desc.length <= 1000) {
            _state.update { it.copy(description = desc) }
        }
    }

    fun selectCaptain(user: User) {
        _state.update {
            it.copy(
                selectedCaptain = user,
                captainError = null
            )
        }
    }

    fun toggleDropdown() {
        _state.update { it.copy(dropdownExpanded = !it.dropdownExpanded) }
    }

    fun createProject() {
        val currentState = _state.value

        // Validation
        if (currentState.projectName.isBlank()) {
            _state.update { it.copy(nameError = "Proje adı zorunludur") }
            return
        }

        if (currentState.selectedCaptain == null) {
            _state.update { it.copy(captainError = "Kaptan seçimi zorunludur") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val request = CreateProjectRequest(
                name = currentState.projectName.trim(),
                description = currentState.description.trim().takeIf { it.isNotBlank() },
                captainUserId = currentState.selectedCaptain.id
            )

            when (val result = projectRepository.createProject(request)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> { /* Ignore */ }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class CreateProjectState(
    val projectName: String = "",
    val description: String = "",
    val selectedCaptain: User? = null,
    val availableUsers: List<User> = emptyList(),
    val dropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val nameError: String? = null,
    val captainError: String? = null
)