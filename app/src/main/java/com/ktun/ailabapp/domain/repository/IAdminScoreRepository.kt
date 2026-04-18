package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import com.ktun.ailabapp.util.NetworkResult

interface IAdminScoreRepository {
    suspend fun getPendingTasks(): NetworkResult<List<PendingTaskResponse>>
    suspend fun assignScore(taskId: String, scoreCategory: Int): NetworkResult<Unit>
    suspend fun adjustUserScore(userId: String, amount: Double, reason: String): NetworkResult<Unit>
}
