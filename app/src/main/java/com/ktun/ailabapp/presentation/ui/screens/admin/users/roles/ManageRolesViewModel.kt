package com.ktun.ailabapp.presentation.ui.screens.admin.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.data.remote.api.ProjectApi
import com.ktun.ailabapp.data.remote.dto.request.TransferOwnershipRequest
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.data.repository.UserRepository
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
    private val projectApi: ProjectApi,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageRolesUiState())
    val uiState: StateFlow<ManageRolesUiState> = _uiState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }

            // Önce kullanıcı bilgisini al
            when (val result = userRepository.getUserById(userId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(user = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoadingUser = false, errorMessage = result.message)
                    }
                    return@launch
                }
                is NetworkResult.Loading -> {}
            }

            // Sonra projeleri doğrudan API'den çek ve captainNames listesinden kontrol et
            try {
                val userFullName = _uiState.value.user?.fullName
                val response = projectApi.getUserProjects(userId)
                if (response.isSuccessful && response.body() != null) {
                    val allProjects = response.body()!!
                    // API captainNames (isim listesi) dönüyor, captains (member listesi) değil
                    val captainProjects = allProjects.filter { project ->
                        project.captainNames.any { name ->
                            name.equals(userFullName, ignoreCase = true)
                        }
                    }.map { UserProject(id = it.id, name = it.name, role = "Captain") }

                    Logger.d("Kullanıcı projeleri: ${allProjects.size}, Kaptan olduğu: ${captainProjects.size}, fullName: $userFullName")

                    _uiState.update {
                        it.copy(
                            captainProjects = captainProjects,
                            isLoadingUser = false
                        )
                    }
                } else {
                    Logger.e("Projeler yüklenemedi: ${response.code()}")
                    _uiState.update {
                        it.copy(captainProjects = emptyList(), isLoadingUser = false)
                    }
                }
            } catch (e: Exception) {
                Logger.e("Proje yükleme hatası: ${e.message}")
                _uiState.update {
                    it.copy(captainProjects = emptyList(), isLoadingUser = false)
                }
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
            try {
                val response = projectApi.getProjectMembers(projectId)
                if (response.isSuccessful && response.body() != null) {
                    val currentUserId = _uiState.value.user?.id
                    // Kaptan hariç üyeleri göster (yeni kaptan olacak kişiler)
                    val members = response.body()!!.filter { it.userId != currentUserId }
                    _uiState.update {
                        it.copy(projectMembers = members, isLoadingMembers = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingMembers = false,
                            errorMessage = "Proje üyeleri yüklenemedi: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingMembers = false, errorMessage = "Hata: ${e.message}")
                }
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

            try {
                val request = TransferOwnershipRequest(
                    currentCaptainId = currentCaptainId,
                    newCaptainId = newCaptainId
                )
                val response = projectApi.transferOwnership(projectId, request)

                if (response.isSuccessful) {
                    Logger.d("Kaptan değişimi başarılı: $currentCaptainId → $newCaptainId")
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Logger.e("Kaptan değişimi başarısız: ${response.code()} - $errorBody")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Kaptan değişimi başarısız: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e("Kaptan değişimi hatası: ${e.message}")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Hata: ${e.message}")
                }
            }
        }
    }
}
