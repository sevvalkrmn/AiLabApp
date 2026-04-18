package com.ktun.ailabapp.domain.usecase.task

import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetUserTaskHistoryUseCase @Inject constructor(
    private val repository: ITaskRepository
) {
    suspend operator fun invoke(userId: String): NetworkResult<List<TaskHistory>> =
        repository.getUserTaskHistory(userId)
}
