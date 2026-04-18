package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(request: CreateProjectRequest): NetworkResult<ProjectDetailResponse> =
        repository.createProject(request)
}
