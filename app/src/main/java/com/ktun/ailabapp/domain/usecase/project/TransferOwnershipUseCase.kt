package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class TransferOwnershipUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(projectId: String, currentCaptainId: String, newCaptainId: String): NetworkResult<Unit> =
        repository.transferOwnership(projectId, currentCaptainId, newCaptainId)
}
