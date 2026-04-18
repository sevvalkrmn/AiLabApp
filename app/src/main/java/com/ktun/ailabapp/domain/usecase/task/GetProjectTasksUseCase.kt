package com.ktun.ailabapp.domain.usecase.task

import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetProjectTasksUseCase @Inject constructor(
    private val repository: ITaskRepository
) {
    suspend operator fun invoke(projectId: String): NetworkResult<List<TaskResponse>> =
        repository.getProjectTasks(projectId)
}
