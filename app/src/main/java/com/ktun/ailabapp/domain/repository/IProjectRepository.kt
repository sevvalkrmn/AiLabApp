package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.util.NetworkResult

interface IProjectRepository {
    suspend fun getMyProjects(roleFilter: String?): NetworkResult<List<MyProjectsResponse>>
    suspend fun getAllProjects(): NetworkResult<List<MyProjectsResponse>>
    suspend fun getUserProjects(userId: String): NetworkResult<List<UserProject>>
    suspend fun getProjectDetail(projectId: String): NetworkResult<ProjectDetailResponse>
    suspend fun getProjectMembers(projectId: String): NetworkResult<List<ProjectMember>>
    suspend fun createProject(request: CreateProjectRequest): NetworkResult<ProjectDetailResponse>
    suspend fun addMember(projectId: String, request: AddMemberRequest): NetworkResult<ProjectMember>
    suspend fun removeMember(projectId: String, userId: String): NetworkResult<Unit>
    suspend fun deleteProject(projectId: String): NetworkResult<Unit>
    suspend fun transferOwnership(projectId: String, currentCaptainId: String, newCaptainId: String): NetworkResult<Unit>
    fun clearCache()
    fun invalidateProjectDetail(projectId: String)
}
