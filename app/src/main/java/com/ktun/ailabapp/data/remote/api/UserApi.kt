// data/remote/api/UsersApi.kt

package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.response.PaginatedResponse
import com.ktun.ailabapp.data.remote.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersApi {
    // ✅ Liste endpoint'i
    @GET("api/Users")
    suspend fun getAllUsers(
        @Query("PageNumber") pageNumber: Int = 1,
        @Query("PageSize") pageSize: Int = 50
    ): Response<PaginatedResponse<UserResponse>>

    // ✅ TEK KULLANICI - Path parameter ile
    @GET("api/Users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<UserResponse>
}