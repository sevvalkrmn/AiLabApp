package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<MyProjectsResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFilter: ProjectFilter = ProjectFilter.ALL
)

enum class ProjectFilter {
    ALL,
    CAPTAIN,
    MEMBER
}

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        loadUserIdAndProjects()
    }

    private fun loadUserIdAndProjects() {
        viewModelScope.launch {
            // Önce kullanıcının ID'sini al
            when (val profileResult = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    profileResult.data?.let { profile ->
                        currentUserId = profile.id
                        android.util.Log.d("ProjectsViewModel", "Current User ID: $currentUserId")

                        // UserId alındıktan sonra projeleri yükle
                        loadProjects()
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectsViewModel", "Profile alınamadı: ${profileResult.message}")
                    // Yine de projeleri yüklemeyi dene
                    loadProjects()
                }
                else -> {}
            }
        }
    }

    fun loadProjects(filter: ProjectFilter = ProjectFilter.ALL) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                selectedFilter = filter
            )

            val roleFilter = when (filter) {
                ProjectFilter.CAPTAIN -> "Captain"
                ProjectFilter.MEMBER -> "Member"
                ProjectFilter.ALL -> null
            }

            when (val result = projectRepository.getMyProjects(roleFilter)) {
                is NetworkResult.Success -> {
                    result.data?.let { projects ->
                        android.util.Log.d("ProjectsViewModel", "Projeler yüklendi: ${projects.size}")

                        // Her proje için üye bilgisini çek ve kullanıcının rolünü bul
                        val projectsWithRoles = mutableListOf<MyProjectsResponse>()

                        projects.forEach { project ->
                            // Proje üyelerini çek
                            when (val membersResult = projectRepository.getProjectMembers(project.id)) {
                                is NetworkResult.Success -> {
                                    membersResult.data?.let { members ->
                                        // Giriş yapan kullanıcının bu projedeki rolünü bul
                                        val userMember = members.find { it.userId == currentUserId }
                                        val userRole = userMember?.role ?: "Member"

                                        projectsWithRoles.add(project.copy(userRole = userRole))

                                        android.util.Log.d("ProjectsViewModel", """
                                            Proje: ${project.name}
                                            User ID: $currentUserId
                                            User Role: $userRole
                                        """.trimIndent())
                                    }
                                }
                                is NetworkResult.Error -> {
                                    android.util.Log.e("ProjectsViewModel", "Üyeler alınamadı: ${membersResult.message}")
                                    // Üyeler alınamazsa default role kullan
                                    projectsWithRoles.add(project.copy(userRole = "Member"))
                                }
                                else -> {}
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            projects = projectsWithRoles,
                            isLoading = false,
                            errorMessage = null
                        )

                        android.util.Log.d("ProjectsViewModel", "Tüm projeler rolleriyle yüklendi: ${projectsWithRoles.size}")
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectsViewModel", "Proje yükleme hatası: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun refreshProjects() {
        loadProjects(_uiState.value.selectedFilter)
    }

    fun filterProjects(filter: ProjectFilter) {
        loadProjects(filter)
    }
}