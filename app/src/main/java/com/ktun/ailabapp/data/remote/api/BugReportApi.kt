package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.CreateBugReportRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BugReportApi {

    @POST("api/BugReports")
    suspend fun createBugReport(
        @Body request: CreateBugReportRequest
    ): Response<Unit>
}
