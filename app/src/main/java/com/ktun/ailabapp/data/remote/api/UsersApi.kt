package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageResponse
import com.ktun.ailabapp.data.remote.dto.response.PaginatedResponse
import com.ktun.ailabapp.data.remote.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
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

    @PUT("api/users/{id}/image")
    suspend fun updateUserProfileImage(
        @Path("id") userId: String,
        @Body request: UpdateProfileImageRequest
    ): Response<UpdateProfileImageResponse>

    // ✅ Kullanıcı Sil
    @DELETE("api/Users/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<Unit>
}
