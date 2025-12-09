package com.ktunailab.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktunailab.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktunailab.ailabapp.data.remote.dto.response.TaskResponse
import com.ktunailab.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktunailab.ailabapp.data.repository.ProjectRepository
import com.ktunailab.ailabapp.data.repository.TaskRepository
import com.ktunailab.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: ProjectDetailResponse? = null,
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

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

                        // ✅ Kullanıcının bu projedeki görevlerini çek
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

    // ✅ GÜNCELLENMIŞ: Kullanıcının görevlerini çek ve filtrele
    private fun loadProjectTasks(projectId: String) {
        viewModelScope.launch {
            android.util.Log.d("ProjectDetailViewModel", "🔵 loadProjectTasks() başladı - ProjectID: $projectId")

            // ✅ DOĞRU API: Kullanıcının TÜM görevlerini çek
            when (val tasksResult = taskRepository.getMyTasks(status = null)) {
                is NetworkResult.Success -> {
                    tasksResult.data?.let { allMyTasks ->
                        // ✅ Sadece bu projeye ait görevleri filtrele
                        val projectTasks = allMyTasks.filter { task ->
                            task.projectId == projectId
                        }

                        // Manuel olarak istatistikleri hesapla (sadece kullanıcının görevleri)
                        val total = projectTasks.size
                        val todo = projectTasks.count { it.status == "Todo" }
                        val inProgress = projectTasks.count { it.status == "InProgress" }
                        val done = projectTasks.count { it.status == "Done" }

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
                                tasks = projectTasks
                            )
                        }

                        android.util.Log.d("ProjectDetailViewModel", """
                            ✅ Görevler filtrelendi:
                            - Toplam görevim: ${allMyTasks.size}
                            - Bu projedeki görevlerim: ${projectTasks.size}
                            - Proje ID: $projectId
                            Hesaplanan istatistikler (sadece benim görevlerim):
                            - Total: $total
                            - Todo: $todo
                            - InProgress: $inProgress
                            - Done: $done
                        """.trimIndent())
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "❌ Görev yükleme hatası: ${tasksResult.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            android.util.Log.d("ProjectDetailViewModel", "🔄 Görev durumu güncelleniyor: $taskId -> $newStatus")

            when (val result = taskRepository.updateTaskStatus(taskId, newStatus)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("ProjectDetailViewModel", "✅ Görev durumu güncellendi")

                    // Projeyi yenile (istatistikler güncellensin)
                    _uiState.value.project?.let { project ->
                        loadProjectDetail(project.id)
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "❌ Durum güncelleme hatası: ${result.message}")
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