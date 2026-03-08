package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<MyProjectsResponse> = emptyList(),
    val currentUserFullName: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
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

    init {
        viewModelScope.launch {
            loadCurrentUser()
            loadProjectsInternal(ProjectFilter.ALL)
        }
    }

    private suspend fun loadCurrentUser() {
        when (val result = authRepository.getProfile()) {
            is NetworkResult.Success -> {
                result.data?.let { profile ->
                    _uiState.value = _uiState.value.copy(currentUserFullName = profile.fullName)
                }
            }
            is NetworkResult.Error -> {
                Logger.e(">>> [ProjectsVM] loadCurrentUser failed: ${result.message}", tag = "RoleDebug")
            }
            else -> {}
        }
    }

    private suspend fun loadProjectsInternal(filter: ProjectFilter) {
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
                val projects = result.data ?: emptyList()
                Logger.d("Projeler yüklendi: ${projects.size}")
                _uiState.value = _uiState.value.copy(
                    projects = projects,
                    isLoading = false,
                    errorMessage = null
                )
            }
            is NetworkResult.Error -> {
                Logger.e("Proje yükleme hatası: ${result.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
            is NetworkResult.Loading -> {}
        }
    }

    fun loadProjects(filter: ProjectFilter = ProjectFilter.ALL) {
        viewModelScope.launch { loadProjectsInternal(filter) }
    }

    fun refreshProjects() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                projectRepository.clearCache()
                loadProjectsInternal(_uiState.value.selectedFilter)
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun filterProjects(filter: ProjectFilter) {
        loadProjects(filter)
    }

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }
}
