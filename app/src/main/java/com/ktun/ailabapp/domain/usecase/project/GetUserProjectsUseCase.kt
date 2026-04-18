package com.ktun.ailabapp.domain.usecase.project

import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class GetUserProjectsUseCase @Inject constructor(
    private val repository: IProjectRepository
) {
    suspend operator fun invoke(userId: String): NetworkResult<List<UserProject>> =
        repository.getUserProjects(userId)
}
