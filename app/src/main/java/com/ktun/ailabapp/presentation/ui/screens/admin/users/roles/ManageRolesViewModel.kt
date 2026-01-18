// presentation/ui/screens/admin/roles/ManageRolesViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.api.RoleApi
import com.ktun.ailabapp.data.remote.dto.request.AssignRoleRequest
import com.ktun.ailabapp.data.remote.dto.request.RemoveRoleRequest
import com.ktun.ailabapp.data.repository.UserRepository
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
    val selectedProjectId: String? = null,
    val selectedProjectName: String? = null,
    val currentRole: String? = null,
    val availableRoles: List<String> = listOf("Member", "Captain"),
    val isLoadingUser: Boolean = false, // ✅ EKLE
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showCaptainWarning: Boolean = false
)

@HiltViewModel
class ManageRolesViewModel @Inject constructor(
    private val roleApi: RoleApi,
    private val userRepository: UserRepository // ✅ EKLE
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageRolesUiState())
    val uiState: StateFlow<ManageRolesUiState> = _uiState.asStateFlow()

    // ✅ YENİ FONKSIYON - userId ile user fetch et
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }

            when (val result = userRepository.getUserById(userId)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            user = result.data,
                            isLoadingUser = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingUser = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun selectProject(projectId: String, projectName: String, currentRole: String?) {
        _uiState.update {
            it.copy(
                selectedProjectId = projectId,
                selectedProjectName = projectName,
                currentRole = currentRole,
                showCaptainWarning = false
            )
        }
    }

    fun selectRole(newRole: String) {
        val currentRole = _uiState.value.currentRole

        if (newRole == "Captain" && currentRole != "Captain") {
            _uiState.update { it.copy(showCaptainWarning = true) }
        } else {
            applyRoleChange(newRole)
        }
    }

    fun confirmCaptainAssignment(newRole: String) {
        _uiState.update { it.copy(showCaptainWarning = false) }
        applyRoleChange(newRole)
    }

    fun dismissCaptainWarning() {
        _uiState.update { it.copy(showCaptainWarning = false) }
    }

    private fun applyRoleChange(newRole: String) {
        val userId = _uiState.value.user?.id ?: return
        val currentRole = _uiState.value.currentRole

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 1. Önce mevcut rolü kaldır (eğer varsa)
                if (currentRole != null && currentRole != "Member") {
                    val removeResponse = roleApi.removeRole(
                        RemoveRoleRequest(userId = userId, roleName = currentRole)
                    )

                    if (!removeResponse.isSuccessful) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Mevcut rol kaldırılamadı: ${removeResponse.code()}"
                            )
                        }
                        return@launch
                    }
                }

                // 2. Yeni rolü ata (eğer Member değilse)
                if (newRole != "Member") {
                    val assignResponse = roleApi.assignRole(
                        AssignRoleRequest(userId = userId, roleName = newRole)
                    )

                    if (assignResponse.isSuccessful) {
                        android.util.Log.d("ManageRolesVM", "✅ Role changed: $currentRole → $newRole")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                currentRole = newRole
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Rol atanamadı: ${assignResponse.code()}"
                            )
                        }
                    }
                } else {
                    android.util.Log.d("ManageRolesVM", "✅ Role removed: $currentRole → Member")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            currentRole = "Member"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ManageRolesVM", "❌ Error: ${e.message}")
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