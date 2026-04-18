package com.ktun.ailabapp.domain.usecase.admin.score

import com.ktun.ailabapp.domain.repository.IAdminScoreRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class AssignScoreUseCase @Inject constructor(
    private val repository: IAdminScoreRepository
) {
    suspend operator fun invoke(taskId: String, scoreCategory: Int): NetworkResult<Unit> =
        repository.assignScore(taskId, scoreCategory)
}
