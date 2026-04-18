package com.ktun.ailabapp.domain.usecase.task

import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: ITaskRepository
) {
    suspend operator fun invoke(taskId: String): NetworkResult<Unit> =
        repository.deleteTask(taskId)
}
