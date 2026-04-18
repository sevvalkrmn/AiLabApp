package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    val isAdmin: Boolean = false,
    val isCaptain: Boolean = false,
    val canEdit: Boolean = false,
    val showAddMemberDialog: Boolean = false,
    val showRemoveMemberDialog: Boolean = false,
    val showDeleteProjectDialog: Boolean = false,
    val showCreateTaskDialog: Boolean = false,
    val showSendAnnouncementDialog: Boolean = false,
    val availableUsers: List<User> = emptyList(),
    val announcementMessage: String? = null,
    val isAnnouncementSending: Boolean = false,
    val selectedTask: TaskResponse? = null,
    val isTaskDetailLoading: Boolean = false
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: IProjectRepository,
    private val taskRepository: ITaskRepository,
    private val userRepository: IUserRepository,
    private val announcementRepository: IAnnouncementRepository,
    private val authRepository: IAuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    fun loadProjectDetail(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val currentUserId = preferencesManager.getUserId()

            val profileDeferred = async { authRepository.getProfile() }
            val projectDeferred = async { projectRepository.getProjectDetail(projectId) }

            val profileResult = profileDeferred.await()
            val projectResult = projectDeferred.await()

            var isAdmin = _uiState.value.isAdmin
            when (profileResult) {
                is NetworkResult.Success -> {
                    isAdmin = profileResult.data?.roles?.any { it.equals("Admin", ignoreCase = true) } ?: false
                }
                else -> {}
            }

            when (projectResult) {
                is NetworkResult.Success -> {
                    projectResult.data?.let { project ->
                        val isCaptain = project.captains.any { it.userId == currentUserId }
                        val canEdit = isAdmin

                        _uiState.value = _uiState.value.copy(
                            project = project,
                            isAdmin = isAdmin,
                            isCaptain = isCaptain,
                            canEdit = canEdit,
                            isLoading = false
                        )

                        loadProjectTasks(projectId)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = projectResult.message)
                }
                else -> {}
            }
        }
    }

    private fun loadProjectTasks(projectId: String) {
        viewModelScope.launch {
            when (val result = taskRepository.getMyTasks(null)) {
                is NetworkResult.Success -> {
                    result.data?.let { allMyTasks ->
                        val currentProjectName = _uiState.value.project?.name

                        val projectTasks = if (currentProjectName != null) {
                            allMyTasks.filter { task -> task.projectName == currentProjectName }
                        } else {
                            emptyList()
                        }

                        val total = projectTasks.size
                        val todo = projectTasks.count { it.status == "Todo" }
                        val inProgress = projectTasks.count { it.status == "InProgress" }
                        val done = projectTasks.count { it.status == "Done" }

                        val calculatedStats = TaskStatistics(total = total, todo = todo, inProgress = inProgress, done = done)

                        _uiState.value.project?.let { project ->
                            val updatedProject = project.copy(taskStatistics = calculatedStats)
                            _uiState.value = _uiState.value.copy(project = updatedProject, tasks = projectTasks)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun loadTaskDetail(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTaskDetailLoading = true) }

            when (val result = taskRepository.getTaskDetail(taskId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isTaskDetailLoading = false, selectedTask = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isTaskDetailLoading = false, errorMessage = "Görev detayı alınamadı: ${result.message}") }
                }
                else -> {}
            }
        }
    }

    fun clearSelectedTask() {
        _uiState.update { it.copy(selectedTask = null) }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            when (val result = taskRepository.updateTaskStatus(taskId, newStatus)) {
                is NetworkResult.Success -> {
                    _uiState.value.project?.let { project ->
                        projectRepository.invalidateProjectDetail(project.id)
                        loadProjectDetail(project.id)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun refreshProject() {
        _uiState.value.project?.let { project ->
            projectRepository.invalidateProjectDetail(project.id)
            loadProjectDetail(project.id)
        }
    }

    fun showAddMemberDialog() {
        loadAvailableUsers()
        _uiState.update { it.copy(showAddMemberDialog = true) }
    }

    fun hideAddMemberDialog() {
        _uiState.update { it.copy(showAddMemberDialog = false) }
    }

    fun showRemoveMemberDialog() { _uiState.update { it.copy(showRemoveMemberDialog = true) } }
    fun hideRemoveMemberDialog() { _uiState.update { it.copy(showRemoveMemberDialog = false) } }
    fun showDeleteProjectDialog() { _uiState.update { it.copy(showDeleteProjectDialog = true) } }
    fun hideDeleteProjectDialog() { _uiState.update { it.copy(showDeleteProjectDialog = false) } }
    fun showCreateTaskDialog() { _uiState.update { it.copy(showCreateTaskDialog = true) } }
    fun hideCreateTaskDialog() { _uiState.update { it.copy(showCreateTaskDialog = false) } }

    fun createTask(title: String, description: String?, assigneeId: String?, dueDate: String?) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch
            val finalAssigneeId = if (assigneeId.isNullOrBlank()) null else assigneeId

            when (val result = taskRepository.createTask(title, description, projectId, finalAssigneeId, dueDate)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showCreateTaskDialog = false, errorMessage = null) }
                    projectRepository.invalidateProjectDetail(projectId)
                    loadProjectDetail(projectId)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun loadAvailableUsers() {
        viewModelScope.launch {
            when (val result = userRepository.getAllUsers(1, 50)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(availableUsers = result.data ?: emptyList()) }
                }
                is NetworkResult.Error -> {
                    Logger.e("Failed to load users: ${result.message}", tag = "ProjectDetailVM")
                }
                else -> {}
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
                    projectRepository.invalidateProjectDetail(projectId)
                    loadProjectDetail(projectId)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch

            when (val result = projectRepository.removeMember(projectId, userId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(showRemoveMemberDialog = false, errorMessage = null) }
                    projectRepository.invalidateProjectDetail(projectId)
                    loadProjectDetail(projectId)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun showSendAnnouncementDialog() { _uiState.update { it.copy(showSendAnnouncementDialog = true) } }

    fun hideSendAnnouncementDialog() {
        _uiState.update { it.copy(showSendAnnouncementDialog = false, announcementMessage = null) }
    }

    fun clearAnnouncementMessage() {
        _uiState.update { it.copy(announcementMessage = null) }
    }

    fun sendTeamAnnouncement(title: String, content: String) {
        val projectId = _uiState.value.project?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAnnouncementSending = true) }
            val result = announcementRepository.createAnnouncement(title, content, 1, null, projectId)
            _uiState.update {
                it.copy(
                    isAnnouncementSending = false,
                    showSendAnnouncementDialog = if (result.isSuccess) false else true,
                    announcementMessage = if (result.isSuccess) "Bildirim tüm takıma gönderildi!" else "Hata: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun sendPersonalAnnouncement(title: String, content: String, targetUserId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnnouncementSending = true) }
            val result = announcementRepository.createAnnouncement(title, content, 2, targetUserId, null)
            _uiState.update {
                it.copy(
                    isAnnouncementSending = false,
                    showSendAnnouncementDialog = if (result.isSuccess) false else true,
                    announcementMessage = if (result.isSuccess) "Bildirim kişiye gönderildi!" else "Hata: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun deleteProject(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val projectId = _uiState.value.project?.id ?: return@launch

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
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }
}
