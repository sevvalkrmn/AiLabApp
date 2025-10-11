package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import com.ktun.ailabapp.R
import com.ktun.ailabapp.data.model.Project
import com.ktun.ailabapp.data.model.ProjectsUiState
import com.ktun.ailabapp.data.model.ProjectStatus
import com.ktun.ailabapp.data.model.ProjectFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        _uiState.update { it.copy(isLoading = true) }

        // Örnek projeler
        val sampleProjects = listOf(
            Project(
                id = "1",
                title = "Ai Lab - Demirağ",
                description = "TEKNOFEST Savaşan İHA Yarışması",
                logoResId = R.drawable.teknofest_logo,
                progress = 0.65f,
                status = ProjectStatus.IN_PROGRESS,
                category = "TEKNOFEST",
                dueDate = "15 Kasım 2024"
            ),
            Project(
                id = "2",
                title = "Tübitak 2209-A",
                description = "Sürü İHA'lar ile Orman Yangınlarına Müdehale Projesi",
                logoResId = R.drawable.tubitak_logo,
                progress = 0.85f,
                status = ProjectStatus.TESTING,
                category = "TÜBİTAK",
                dueDate = "30 Ekim 2024"
            ),
            Project(
                id = "3",
                title = "Ai Lab - Yalkın",
                description = "TEKNOFEST Sürü İHA Yarışması",
                logoResId = R.drawable.teknofest_logo,
                progress = 0.45f,
                status = ProjectStatus.IN_PROGRESS,
                category = "TEKNOFEST",
                dueDate = "20 Aralık 2024"
            )
        )

        _uiState.update {
            it.copy(
                projects = sampleProjects,
                isLoading = false
            )
        }
    }

    fun setFilter(filter: ProjectFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredProjects(): List<Project> {
        return when (_uiState.value.selectedFilter) {
            ProjectFilter.ALL -> _uiState.value.projects
            ProjectFilter.ACTIVE -> _uiState.value.projects.filter {
                it.status == ProjectStatus.IN_PROGRESS || it.status == ProjectStatus.TESTING
            }
            ProjectFilter.COMPLETED -> _uiState.value.projects.filter {
                it.status == ProjectStatus.COMPLETED
            }
        }
    }
}