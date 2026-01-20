package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.CreateTaskRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateTaskRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateTaskStatusRequest
import com.ktun.ailabapp.data.remote.dto.response.PaginatedResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskHistoryItem
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import retrofit2.Response
import retrofit2.http.*

interface TaskApi {

    /**
     * Belirli bir projenin tüm görevlerini listele
     * GET /api/Tasks/project/{projectId}
     */
    @GET("api/Tasks/project/{projectId}")
    suspend fun getProjectTasks(
        @Path("projectId") projectId: String,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<PaginatedResponse<TaskResponse>>

    /**
     * Belirli bir görevin detaylarını getir
     * GET /api/Tasks/{id}
     */
    @GET("api/Tasks/{id}")
    suspend fun getTaskDetail(
        @Path("id") taskId: String
    ): Response<TaskResponse>

    /**
     * Kullanıcıya atanan görevleri listele
     * GET /api/Tasks/my-tasks
     */
    @GET("api/Tasks/my-tasks")
    suspend fun getMyTasks(
        @Query("status") status: Int? = null
    ): Response<List<TaskResponse>>

    /**
     * Yeni görev oluştur (Admin veya Captain)
     * POST /api/Tasks
     */
    @POST("api/Tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): Response<TaskResponse>

    /**
     * Görevi güncelle (Admin veya Captain)
     * PUT /api/Tasks/{id}
     */
    @PUT("api/Tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body request: UpdateTaskRequest
    ): Response<TaskResponse>

    /**
     * Görev durumunu değiştir (Atanan kişi, Admin veya Captain)
     * PUT /api/Tasks/{id}/status
     */
    @PUT("api/Tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") taskId: String,
        @Body request: UpdateTaskStatusRequest
    ): Response<TaskResponse>

    /**
     * Görevi sil (Admin veya Captain)
     * DELETE /api/Tasks/{id}
     */
    @DELETE("api/Tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: String
    ): Response<Unit>

    @GET("api/Tasks/user/{userId}/history")
    suspend fun getUserTaskHistory(
        @Path("userId") userId: String
    ): Response<List<TaskHistoryItem>>
}