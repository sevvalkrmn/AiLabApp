package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.BugReportApi
import com.ktun.ailabapp.data.remote.dto.request.CreateBugReportRequest
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BugReportRepository @Inject constructor(
    private val bugReportApi: BugReportApi
) {

    suspend fun createBugReport(
        bugType: Int,
        pageInfo: String,
        description: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = CreateBugReportRequest(
                platform = 2, // Mobile
                bugType = bugType,
                pageInfo = pageInfo,
                description = description
            )

            val response = bugReportApi.createBugReport(request)

            when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 429 -> NetworkResult.Error("Çok fazla istek gönderdiniz. Lütfen bekleyin.")
                response.isSuccessful -> NetworkResult.Success(Unit)
                else -> NetworkResult.Error("Hata bildirimi gönderilemedi: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}