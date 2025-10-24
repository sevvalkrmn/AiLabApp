package com.ktun.ailabapp.presentation.ui.screens.projects

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
    val project: ProjectDetailResponse? = null,
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProjectDetailViewModel(application: Application) : ViewModel() {

    private val projectRepository = ProjectRepository(application.applicationContext)
    private val taskRepository = TaskRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    fun loadProjectDetail(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Proje detayını çek
            when (val projectResult = projectRepository.getProjectDetail(projectId)) {
                is NetworkResult.Success -> {
                    projectResult.data?.let { project ->
                        _uiState.value = _uiState.value.copy(
                            project = project,
                            isLoading = false
                        )

                        android.util.Log.d("ProjectDetailViewModel", """
                            Proje yüklendi: ${project.name}
                            Backend taskStatistics: ${project.taskStatistics}
                        """.trimIndent())

                        // Proje görevlerini çek
                        loadProjectTasks(projectId)
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "Proje yükleme hatası: ${projectResult.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = projectResult.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadProjectTasks(projectId: String) {
        viewModelScope.launch {
            when (val tasksResult = taskRepository.getProjectTasks(projectId)) {
                is NetworkResult.Success -> {
                    tasksResult.data?.let { tasks ->
                        // Manuel olarak istatistikleri hesapla
                        val total = tasks.size
                        val todo = tasks.count { it.status == "Todo" }
                        val inProgress = tasks.count { it.status == "InProgress" }
                        val done = tasks.count { it.status == "Done" }

                        val calculatedStats = TaskStatistics(
                            total = total,
                            todo = todo,
                            inProgress = inProgress,
                            done = done
                        )

                        // Proje bilgisini güncelle - taskStatistics'i override et
                        _uiState.value.project?.let { project ->
                            val updatedProject = project.copy(taskStatistics = calculatedStats)
                            _uiState.value = _uiState.value.copy(
                                project = updatedProject,
                                tasks = tasks
                            )
                        }

                        android.util.Log.d("ProjectDetailViewModel", """
                            Görevler yüklendi: ${tasks.size}
                            Hesaplanan istatistikler:
                            - Total: $total
                            - Todo: $todo
                            - InProgress: $inProgress
                            - Done: $done
                        """.trimIndent())
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "Görev yükleme hatası: ${tasksResult.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            when (val result = taskRepository.updateTaskStatus(taskId, newStatus)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("ProjectDetailViewModel", "Görev durumu güncellendi")

                    // Projeyi yenile (istatistikler güncellensin)
                    _uiState.value.project?.let { project ->
                        loadProjectDetail(project.id)
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "Durum güncelleme hatası: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun refreshProject() {
        _uiState.value.project?.let { project ->
            loadProjectDetail(project.id)
        }
    }
}

class ProjectDetailViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectDetailViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}