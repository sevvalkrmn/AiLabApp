package com.ktun.ailabapp.data.model

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: ProjectFilter = ProjectFilter.ALL
)

data class Project(
    val id: String,
    val title: String,
    val description: String,
    val logoResId: Int? = null,
    val logoLetter: String = "A",
    val progress: Float = 0f,
    val status: ProjectStatus = ProjectStatus.IN_PROGRESS,
    val dueDate: String? = null,
    val category: String = ""
)

enum class ProjectStatus {
    IN_PROGRESS,
    TESTING,
    COMPLETED,
    CANCELLED
}

enum class ProjectFilter {
    ALL,
    ACTIVE,
    COMPLETED
}