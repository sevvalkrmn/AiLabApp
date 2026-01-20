// data/remote/api/ProjectApi.kt

package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateMemberRoleRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateProjectRequest
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import retrofit2.Response
import retrofit2.http.*

interface ProjectApi {

    /**
     * Kullanıcının üyesi olduğu projeleri listele
     * GET /api/projects/my-projects
     */
    @GET("api/projects/my-projects")
    suspend fun getMyProjects(
        @Query("role") role: String? = null  // "Captain" veya "Member" filtresi
    ): Response<List<MyProjectsResponse>>

    // ✅ YENİ ENDPOINT - Belirli bir kullanıcının projelerini getir
    /**
     * Belirli bir kullanıcının projelerini getir (Admin)
     * GET /api/projects/user/{userId}
     */
    @GET("api/projects/user/{userId}")
    suspend fun getUserProjects(
        @Path("userId") userId: String
    ): Response<List<ProjectDetailResponse>>

    /**
     * Belirli bir projenin detaylarını getir
     * GET /api/projects/{id}
     */
    @GET("api/projects/{id}")
    suspend fun getProjectDetail(
        @Path("id") projectId: String
    ): Response<ProjectDetailResponse>

    /**
     * Yeni proje oluştur (Sadece Admin)
     * POST /api/projects
     */
    @POST("api/projects")
    suspend fun createProject(
        @Body request: CreateProjectRequest
    ): Response<ProjectDetailResponse>

    /**
     * Proje bilgilerini güncelle (Admin veya Captain)
     * PUT /api/projects/{id}
     */
    @PUT("api/projects/{id}")
    suspend fun updateProject(
        @Path("id") projectId: String,
        @Body request: UpdateProjectRequest
    ): Response<ProjectDetailResponse>

    /**
     * Projeyi sil (Sadece Admin)
     * DELETE /api/projects/{id}
     */
    @DELETE("api/projects/{id}")
    suspend fun deleteProject(
        @Path("id") projectId: String
    ): Response<Unit>

    /**
     * Proje üyelerini listele
     * GET /api/projects/{id}/members
     */
    @GET("api/projects/{id}/members")
    suspend fun getProjectMembers(
        @Path("id") projectId: String
    ): Response<List<ProjectMember>>

    /**
     * Projeye üye ekle (Admin veya Captain)
     * POST /api/projects/{id}/members
     */
    @POST("api/projects/{id}/members")
    suspend fun addMember(
        @Path("id") projectId: String,
        @Body request: AddMemberRequest
    ): Response<Unit>

    /**
     * Projeden üye çıkar (Admin veya Captain)
     * DELETE /api/projects/{id}/members/{userId}
     */
    @DELETE("api/projects/{id}/members/{userId}")
    suspend fun removeMember(
        @Path("id") projectId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    /**
     * Üye rolünü değiştir (Sadece Admin)
     * PUT /api/projects/{id}/members/{userId}/role
     */
    @PUT("api/projects/{id}/members/{userId}/role")
    suspend fun updateMemberRole(
        @Path("id") projectId: String,
        @Path("userId") userId: String,
        @Body request: UpdateMemberRoleRequest
    ): Response<Unit>

}