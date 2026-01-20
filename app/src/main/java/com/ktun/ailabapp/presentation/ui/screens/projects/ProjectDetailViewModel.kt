package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.data.repository.UserRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: ProjectDetailResponse? = null,
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // ✅ YENİ - Admin ve Kaptan işlemleri
    val isAdmin: Boolean = false,
    val isCaptain: Boolean = false, // ✅ Kaptan kontrolü
    val canEdit: Boolean = false,   // ✅ Yetki kontrolü (Admin veya Kaptan)
    
    val showAddMemberDialog: Boolean = false,
    val showRemoveMemberDialog: Boolean = false,
    val showDeleteProjectDialog: Boolean = false,
    val showCreateTaskDialog: Boolean = false, // ✅ YENİ
    val availableUsers: List<User> = emptyList()
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.d("ProjectDetailViewModel", "🎬 ViewModel CREATED")
        checkAdminStatus()
    }

    // ✅ YENİ - Admin kontrolü
    private fun checkAdminStatus() {
        viewModelScope.launch {
            // ✅ suspend function olduğu için direkt çağır
            val userId = preferencesManager.getUserId()

            if (userId != null) {
                when (val result = userRepository.getUserById(userId)) {
                    is NetworkResult.Success -> {
                        val user = result.data
                        val isAdmin = user?.roles?.any {
                            it.equals("Admin", ignoreCase = true)
                        } ?: false

                        android.util.Log.d("ProjectDetailViewModel", "👤 User: ${user?.fullName}, Roles: ${user?.roles}, isAdmin: $isAdmin")

                        _uiState.update { 
                            it.copy(
                                isAdmin = isAdmin,
                                canEdit = isAdmin // Varsayılan olarak admin ise yetkili
                            ) 
                        }
                    }
                    is NetworkResult.Error -> {
                        android.util.Log.e("ProjectDetailViewModel", "Failed to load user: ${result.message}")
                        _uiState.update { it.copy(isAdmin = false) }
                    }
                    is NetworkResult.Loading -> {}
                }
            } else {
                android.util.Log.e("ProjectDetailViewModel", "User ID is null")
                _uiState.update { it.copy(isAdmin = false) }
            }
        }
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

                        // ✅ Kaptanlık Kontrolü
                        val currentUserId = preferencesManager.getUserId()
                        val isCaptain = project.captains.any { it.userId == currentUserId }
                        val isAdmin = _uiState.value.isAdmin
                        
                        // Admin veya Kaptan ise düzenleyebilir
                        val canEdit = isAdmin || isCaptain

                        _uiState.value = _uiState.value.copy(
                            project = project,
                            isCaptain = isCaptain,
                            canEdit = canEdit,
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

                        val currentProjectName = _uiState.value.project?.name

                        android.util.Log.d("ProjectDetailViewModel", "🔍 Filtreleme kriteri: projectName = '$currentProjectName'")

                        val projectTasks = if (currentProjectName != null) {
                            allMyTasks.filter { task ->
                                task.projectName == currentProjectName
                            }
                        } else {
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

    // ✅ YENİ - Admin Fonksiyonları

    fun showAddMemberDialog() {
        loadAvailableUsers()
        _uiState.update { it.copy(showAddMemberDialog = true) }
    }

    fun hideAddMemberDialog() {
        _uiState.update { it.copy(showAddMemberDialog = false) }
    }

    fun showRemoveMemberDialog() {
        _uiState.update { it.copy(showRemoveMemberDialog = true) }
    }

    fun hideRemoveMemberDialog() {
        _uiState.update { it.copy(showRemoveMemberDialog = false) }
    }

    fun showDeleteProjectDialog() {
        _uiState.update { it.copy(showDeleteProjectDialog = true) }
    }

    fun hideDeleteProjectDialog() {
        _uiState.update { it.copy(showDeleteProjectDialog = false) }
    }

    // ✅ YENİ: Görev Ekleme Dialog Kontrolleri
    fun showCreateTaskDialog() {
        _uiState.update { it.copy(showCreateTaskDialog = true) }
    }

    fun hideCreateTaskDialog() {
        _uiState.update { it.copy(showCreateTaskDialog = false) }
    }

    fun createTask(title: String, description: String?, assigneeId: String?, dueDate: String?) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch

            // Eğer assigneeId boşsa null gönder
            val finalAssigneeId = if (assigneeId.isNullOrBlank()) null else assigneeId

            when (val result = taskRepository.createTask(title, description, projectId, finalAssigneeId, dueDate)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showCreateTaskDialog = false, errorMessage = null) }
                    loadProjectDetail(projectId) // Listeyi yenile
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadAvailableUsers() {
        viewModelScope.launch {
            when (val result = userRepository.getAllUsers()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(availableUsers = result.data ?: emptyList()) }
                }
                is NetworkResult.Error -> {
                    android.util.Log.e("ProjectDetailViewModel", "❌ Failed to load users: ${result.message}")
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun addMember(userId: String, role: String) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch
            val request = AddMemberRequest(userId, role)

            when (val result = projectRepository.addMember(projectId, request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showAddMemberDialog = false, errorMessage = null) }
                    loadProjectDetail(projectId) // Refresh
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch

            // ✅ Kural 5: Sadece Captain koruması var. (UI'da zaten Captainlar listede yok)
            // Başka bir durum (task vb.) göz önünde bulundurulmamalı dendiği için kontrolü kaldırdık.

            when (val result = projectRepository.removeMember(projectId, userId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showRemoveMemberDialog = false, errorMessage = null) }
                    loadProjectDetail(projectId) // Refresh
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun deleteProject(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch

            // ✅ Kural 6: Projede Tamamlanmamış (Aktif) görevler varsa silme işlemi engellenir.
            val hasIncompleteTasks = _uiState.value.tasks.any { it.status != "Done" }
            
            if (hasIncompleteTasks) {
                _uiState.update { 
                    it.copy(
                        showDeleteProjectDialog = false,
                        errorMessage = "Projeyi silemezsiniz: Tamamlanmamış (Aktif) görevler var. Önce görevleri tamamlayın veya iptal edin."
                    ) 
                }
                return@launch
            }

            when (val result = projectRepository.deleteProject(projectId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showDeleteProjectDialog = false) }
                    onSuccess() // Navigate back
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
