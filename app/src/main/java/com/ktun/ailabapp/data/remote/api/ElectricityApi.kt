package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.ElectricityControlRequest
import com.ktun.ailabapp.data.remote.dto.response.ElectricityControlResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ElectricityApi {

    @POST("api/Electricity/control")
    suspend fun controlDevice(
        @Body request: ElectricityControlRequest
    ): Response<ElectricityControlResponse>
}
