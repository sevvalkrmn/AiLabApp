package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetProjectDetailUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(projectId: String): NetworkResult<ProjectDetailResponse> =
        repository.getProjectDetail(projectId)
}
