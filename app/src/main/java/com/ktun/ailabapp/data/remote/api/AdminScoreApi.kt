package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.AdjustScoreRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AdminScoreApi {

    @POST("api/AdminScore/users/{userId}/adjust-score")
    suspend fun adjustUserScore(
        @Path("userId") userId: String,
        @Body request: AdjustScoreRequest
    ): Response<Unit>
}