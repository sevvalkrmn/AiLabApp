package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.RfidRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RfidApi {
    @POST("register-start")
    suspend fun startRegistration(@Body request: RfidRegisterRequest): Response<Any>
    
    @POST("register-stop")
    suspend fun stopRegistration(): Response<Any>
}
