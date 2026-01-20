package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.AdjustScoreRequest
import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AdminScoreApi {

    @POST("api/AdminScore/users/{userId}/adjust-score")
    suspend fun adjustUserScore(
        @Path("userId") userId: String,
        @Body request: AdjustScoreRequest
    ): Response<Unit>

    // ✅ YENİ: Bekleyen görevleri listele
    @GET("api/AdminScore/pending-tasks")
    suspend fun getPendingTasks(): Response<List<PendingTaskResponse>>

    // ✅ YENİ: Göreve puan ata (Body int olarak gidiyor)
    @POST("api/AdminScore/tasks/{taskId}/assign-score")
    suspend fun assignScore(
        @Path("taskId") taskId: String,
        @Body scoreCategory: Int
    ): Response<Unit>
}