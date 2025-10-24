package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.CreateTaskRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateTaskRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateTaskStatusRequest
import com.ktun.ailabapp.data.remote.dto.response.PaginatedResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import retrofit2.Response
import retrofit2.http.*

interface TaskApi {

    /**
     * Belirli bir projenin tüm görevlerini listele
     * GET /api/tasks/project/{projectId}
     */
    @GET("api/tasks/project/{projectId}")
    suspend fun getProjectTasks(
        @Path("projectId") projectId: String,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<PaginatedResponse<TaskResponse>>  // ← DEĞİŞTİ: List yerine PaginatedResponse

    /**
     * Belirli bir görevin detaylarını getir
     * GET /api/tasks/{id}
     */
    @GET("api/tasks/{id}")
    suspend fun getTaskDetail(
        @Path("id") taskId: String
    ): Response<TaskResponse>

    /**
     * Kullanıcıya atanan görevleri listele
     * GET /api/tasks/my-tasks
     */
    @GET("api/tasks/my-tasks")
    suspend fun getMyTasks(
        @Query("status") status: String? = null
    ): Response<PaginatedResponse<TaskResponse>>  // ← DEĞİŞTİ: List yerine PaginatedResponse

    /**
     * Yeni görev oluştur (Admin veya Captain)
     * POST /api/tasks
     */
    @POST("api/tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): Response<TaskResponse>

    /**
     * Görevi güncelle (Admin veya Captain)
     * PUT /api/tasks/{id}
     */
    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body request: UpdateTaskRequest
    ): Response<TaskResponse>

    /**
     * Görev durumunu değiştir (Atanan kişi, Admin veya Captain)
     * PUT /api/tasks/{id}/status
     */
    @PUT("api/tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") taskId: String,
        @Body request: UpdateTaskStatusRequest
    ): Response<TaskResponse>

    /**
     * Görevi sil (Admin veya Captain)
     * DELETE /api/tasks/{id}
     */
    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: String
    ): Response<Unit>
}