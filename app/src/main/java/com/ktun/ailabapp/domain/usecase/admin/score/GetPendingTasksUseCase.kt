package com.ktun.ailabapp.domain.usecase.admin.score

import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import com.ktun.ailabapp.domain.repository.IAdminScoreRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetPendingTasksUseCase @Inject constructor(
    private val repository: IAdminScoreRepository
) {
    suspend operator fun invoke(): NetworkResult<List<PendingTaskResponse>> =
        repository.getPendingTasks()
}
