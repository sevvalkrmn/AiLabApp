package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.util.NetworkResult

interface IBugReportRepository {
    suspend fun createBugReport(bugType: Int, pageInfo: String, description: String): NetworkResult<Unit>
}
