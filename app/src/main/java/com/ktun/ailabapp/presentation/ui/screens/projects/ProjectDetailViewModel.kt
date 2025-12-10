package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.util.NetworkResult
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

    init {
        android.util.Log.d("ProjectDetailViewModel", "🎬 ViewModel CREATED")
    }

    fun loadProjectDetail(projectId: String) {
        android.util.Log.d("ProjectDetailViewModel", "🔵 ========================================")
        android.util.Log.d("ProjectDetailViewModel", "🔵 loadProjectDetail() ÇAĞRILDI")
        android.util.Log.d("ProjectDetailViewModel", "🔵 ProjectID: $projectId")
        android.util.Log.d("ProjectDetailViewModel", "🔵 ========================================")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            android.util.Log.d("ProjectDetailViewModel", "📥 Proje detayı API çağrısı başlıyor...")

            when (val projectResult = projectRepository.getProjectDetail(projectId)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("ProjectDetailViewModel", "✅ Proje detayı başarılı")

                    projectResult.data?.let { project ->
                        android.util.Log.d("ProjectDetailViewModel", """
                            ✅ Proje yüklendi:
                            - Name: ${project.name}
                            - Members: ${project.members.size}
                            - Captains: ${project.captains.size}
                        """.trimIndent())

                        _uiState.value = _uiState.value.copy(
                            project = project,
                            isLoading = false
                        )

                        android.util.Log.d("ProjectDetailViewModel", "🔄 loadProjectTasks() çağrılıyor...")
                        loadProjectTasks(projectId)
                    } ?: run {
                        android.util.Log.e("ProjectDetailViewModel", "❌ Project data NULL!")
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "❌ Proje yükleme hatası: ${projectResult.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = projectResult.message
                    )
                }
                is NetworkResult.Loading -> {
                    android.util.Log.d("ProjectDetailViewModel", "⏳ Loading state")
                }
            }
        }
    }

    private fun loadProjectTasks(projectId: String) {
        viewModelScope.launch {
            android.util.Log.d("ProjectDetailViewModel", "🔵 loadProjectTasks() BAŞLADI - ProjectID: $projectId")

            when (val result = taskRepository.getMyTasks(status = null)) {
                is NetworkResult.Success -> {
                    result.data?.let { allMyTasks ->
                        android.util.Log.d("ProjectDetailViewModel", "📦 Toplam görev sayısı: ${allMyTasks.size}")

                        // ✅ GEÇİCİ: projectName ile filtrele (projectId null olduğu için)
                        val currentProjectName = _uiState.value.project?.name

                        android.util.Log.d("ProjectDetailViewModel", "🔍 Filtreleme kriteri: projectName = '$currentProjectName'")

                        allMyTasks.forEachIndexed { index, task ->
                            android.util.Log.d("ProjectDetailViewModel", """
                            Görev #${index + 1}:
                            - Title: ${task.title}
                            - ProjectID: '${task.projectId}'
                            - ProjectName: '${task.projectName}'
                            - Expected Name: '$currentProjectName'
                            - Match: ${task.projectName == currentProjectName}
                        """.trimIndent())
                        }

                        val projectTasks = if (currentProjectName != null) {
                            // ✅ GEÇİCİ FIX: projectName ile filtrele
                            allMyTasks.filter { task ->
                                task.projectName == currentProjectName
                            }
                        } else {
                            // projectId null ise boş liste
                            emptyList()
                        }

                        android.util.Log.d("ProjectDetailViewModel", """
                        ✅ Filtreleme tamamlandı:
                        - Toplam görevim: ${allMyTasks.size}
                        - Bu projedeki görevlerim: ${projectTasks.size}
                    """.trimIndent())

                        // İstatistikler hesapla
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

                        _uiState.value.project?.let { project ->
                            val updatedProject = project.copy(taskStatistics = calculatedStats)
                            _uiState.value = _uiState.value.copy(
                                project = updatedProject,
                                tasks = projectTasks
                            )

                            android.util.Log.d("ProjectDetailViewModel", "✅ UI State güncellendi - Tasks: ${projectTasks.size}")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "❌ getMyTasks() ERROR: ${result.message}")
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