package com.ktun.ailabapp.presentation.ui.screens.admin.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.domain.usecase.project.GetProjectMembersUseCase
import com.ktun.ailabapp.domain.usecase.project.GetUserProjectsUseCase
import com.ktun.ailabapp.domain.usecase.project.TransferOwnershipUseCase
import com.ktun.ailabapp.domain.usecase.user.GetUserByIdUseCase
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageRolesUiState(
    val user: User? = null,
    val captainProjects: List<UserProject> = emptyList(),
    val selectedProjectId: String? = null,
    val selectedProjectName: String? = null,
    val projectMembers: List<ProjectMember> = emptyList(),
    val selectedNewCaptain: ProjectMember? = null,
    val isLoadingUser: Boolean = false,
    val isLoadingMembers: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class ManageRolesViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserProjectsUseCase: GetUserProjectsUseCase,
    private val getProjectMembersUseCase: GetProjectMembersUseCase,
    private val transferOwnershipUseCase: TransferOwnershipUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageRolesUiState())
    val uiState: StateFlow<ManageRolesUiState> = _uiState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }

            when (val result = getUserByIdUseCase(userId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(user = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoadingUser = false, errorMessage = result.message) }
                    return@launch
                }
                is NetworkResult.Loading -> {}
            }

            when (val result = getUserProjectsUseCase(userId)) {
                is NetworkResult.Success -> {
                    val userFullName = _uiState.value.user?.fullName
                    val captainProjects = (result.data ?: emptyList()).filter { it.role == "Captain" }
                    Logger.d("Kullanıcı projeleri: ${result.data?.size}, Kaptan olduğu: ${captainProjects.size}, fullName: $userFullName")
                    _uiState.update { it.copy(captainProjects = captainProjects, isLoadingUser = false) }
                }
                is NetworkResult.Error -> {
                    Logger.e("Projeler yüklenemedi: ${result.message}")
                    _uiState.update { it.copy(captainProjects = emptyList(), isLoadingUser = false) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun selectProject(projectId: String, projectName: String) {
        _uiState.update {
            it.copy(
                selectedProjectId = projectId,
                selectedProjectName = projectName,
                selectedNewCaptain = null,
                isLoadingMembers = true,
                errorMessage = null
            )
        }
        loadProjectMembers(projectId)
    }

    private fun loadProjectMembers(projectId: String) {
        viewModelScope.launch {
            when (val result = getProjectMembersUseCase(projectId)) {
                is NetworkResult.Success -> {
                    val currentUserId = _uiState.value.user?.id
                    val members = (result.data ?: emptyList()).filter { it.userId != currentUserId }
                    _uiState.update { it.copy(projectMembers = members, isLoadingMembers = false) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoadingMembers = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun selectNewCaptain(member: ProjectMember) {
        _uiState.update { it.copy(selectedNewCaptain = member) }
    }

    fun showConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = true) }
    }

    fun dismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun transferOwnership() {
        val projectId = _uiState.value.selectedProjectId ?: return
        val currentCaptainId = _uiState.value.user?.id ?: return
        val newCaptainId = _uiState.value.selectedNewCaptain?.userId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, showConfirmDialog = false) }

            when (val result = transferOwnershipUseCase(projectId, currentCaptainId, newCaptainId)) {
                is NetworkResult.Success -> {
                    Logger.d("Kaptan değişimi başarılı: $currentCaptainId → $newCaptainId")
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    Logger.e("Kaptan değişimi başarısız: ${result.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
