package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import com.ktun.ailabapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    fun loadProjectDetails(projectId: String) {
        // Sadece ilk kez yükle
        if (isInitialized) {
            println("✅ Already initialized, skipping")
            return
        }

        println("🔍 loadProjectDetails called - projectId: $projectId")
        println("📥 Loading tasks...")

        _uiState.update { it.copy(isLoading = true) }

        val project = Project(
            id = projectId,
            title = "Ai Lab - Demirağ",
            description = "TEKNOFEST Savaşan İHA Yarışması",
            logoLetter = "A",
            progress = 0.65f,
            status = ProjectStatus.IN_PROGRESS,
            category = "TEKNOFEST"
        )

        val sampleTasks = listOf(
            Task(
                id = "1",
                title = "Rapor Revize",
                description = "Madde 3,4,12,15 gerekli notlara dikkat edilerek düzenlenecek.",
                detayAciklamasi = """
            Proje raporunun aşağıdaki maddeleri gözden geçirilmeli ve güncellenmelidir:
            
            • Madde 3: Proje hedefleri ve kapsamı detaylandırılacak
            • Madde 4: Risk analizi eklenecek
            • Madde 12: Bütçe planlaması güncellenecek
            • Madde 15: Zaman çizelgesi revize edilecek
            
            Lütfen değişiklikleri yaparken akademik yazım kurallarına dikkat edin ve kaynakları belirtin.
        """.trimIndent(),
                takimKaptani = "Ahmet Yılmaz",
                dueDate = "14.12.2025",
                dueTime = "15:00",
                status = TaskStatus.IN_PROGRESS
            ),
            Task(
                id = "2",
                title = "Tasarım Dokümantasyonu",
                description = "Sistem mimarisi ve tasarım kararları dokümante edilecek.",
                detayAciklamasi = """
            Sistem tasarım dokümantasyonu hazırlanacak:
            
            • UML diyagramları oluşturulacak
            • Veritabanı şeması detaylandırılacak
            • API endpoint'leri dokümante edilecek
            • Güvenlik protokolleri açıklanacak
            
            Dokümantasyon IEEE standartlarına uygun olmalıdır.
        """.trimIndent(),
                takimKaptani = "Mehmet Demir",
                dueDate = "14.12.2025",
                dueTime = "15:00",
                status = TaskStatus.IN_PROGRESS
            ),
            Task(
                id = "3",
                title = "Rapor Revize",
                description = "Madde 3,4,12,15 gerekli notlara dikkat edilerek düzenlenecek.",
                dueDate = "14.12.2025",
                dueTime = "15:00",
                status = TaskStatus.TO_DO
            ),
            Task(
                id = "4",
                title = "Rapor Revize",
                description = "Madde 3,4,12,15 gerekli notlara dikkat edilerek düzenlenecek.",
                dueDate = "14.12.2025",
                dueTime = "15:00",
                status = TaskStatus.TO_DO
            ),
            Task(
                id = "5",
                title = "Rapor Revize",
                description = "Madde 3,4,12,15 gerekli notlara dikkat edilerek düzenlenecek.",
                dueDate = "14.12.2025",
                dueTime = "15:00",
                status = TaskStatus.DONE
            )
        )

        _uiState.update {
            it.copy(
                project = project,
                tasks = sampleTasks,
                isLoading = false
            )
        }

        isInitialized = true
        println("✨ Tasks loaded successfully! Total: ${sampleTasks.size}")
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun getFilteredTasks(): List<Task> {
        return when (_uiState.value.selectedFilter) {
            TaskFilter.ALL -> _uiState.value.tasks
            TaskFilter.TO_DO -> _uiState.value.tasks.filter { it.status == TaskStatus.TO_DO }
            TaskFilter.IN_PROGRESS -> _uiState.value.tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            TaskFilter.DONE -> _uiState.value.tasks.filter { it.status == TaskStatus.DONE }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        println("📝 Updating task $taskId to $newStatus")

        _uiState.update { currentState ->
            val updatedTasks = currentState.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(status = newStatus)
                } else {
                    task
                }
            }
            currentState.copy(tasks = updatedTasks)
        }

        println("✅ Task status updated successfully")
    }


}