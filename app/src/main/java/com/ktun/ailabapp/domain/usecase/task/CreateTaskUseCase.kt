package com.ktun.ailabapp.domain.usecase.task

import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        projectId: String,
        assigneeId: String?,
        dueDate: String?
    ): NetworkResult<TaskResponse> = repository.createTask(title, description, projectId, assigneeId, dueDate)
}
