package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class DeleteProjectUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(projectId: String): NetworkResult<Unit> =
        repository.deleteProject(projectId)
}
