package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.AdminScoreApi
import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminScoreRepository @Inject constructor(
    private val adminScoreApi: AdminScoreApi
) {

    suspend fun getPendingTasks(): NetworkResult<List<PendingTaskResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = adminScoreApi.getPendingTasks()

            when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Yetkisiz erişim")
                response.isSuccessful && response.body() != null -> {
                    NetworkResult.Success(response.body()!!)
                }
                else -> NetworkResult.Error("Görevler yüklenemedi: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun assignScore(taskId: String, scoreCategory: Int): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = adminScoreApi.assignScore(taskId, scoreCategory)

            when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Yetkisiz erişim")
                response.code() == 400 -> NetworkResult.Error("Geçersiz puan kategorisi veya görev bulunamadı")
                response.isSuccessful -> NetworkResult.Success(Unit)
                else -> NetworkResult.Error("Puan atanamadı: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}
