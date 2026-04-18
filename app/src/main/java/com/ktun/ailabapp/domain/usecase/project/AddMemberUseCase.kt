package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(projectId: String, request: AddMemberRequest): NetworkResult<ProjectMember> =
        repository.addMember(projectId, request)
}
