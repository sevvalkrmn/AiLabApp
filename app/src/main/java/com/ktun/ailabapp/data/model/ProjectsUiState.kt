package com.ktunailab.ailabapp.data.model

import com.ktunailab.ailabapp.data.remote.dto.response.MyProjectsResponse

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