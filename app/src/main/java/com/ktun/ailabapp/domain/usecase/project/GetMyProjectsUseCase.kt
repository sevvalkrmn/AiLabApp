package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetMyProjectsUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(roleFilter: String? = null): NetworkResult<List<MyProjectsResponse>> =
        repository.getMyProjects(roleFilter)
}
