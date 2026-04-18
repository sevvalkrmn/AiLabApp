package com.ktun.ailabapp.domain.usecase.task

import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetTaskDetailUseCase @Inject constructor(
    private val repository: ITaskRepository
) {
    suspend operator fun invoke(taskId: String): NetworkResult<TaskResponse> =
        repository.getTaskDetail(taskId)
}
