// data/remote/api/RoleApi.kt

package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.request.AssignRoleRequest
import com.ktun.ailabapp.data.remote.dto.request.RemoveRoleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RoleApi {

    @POST("api/roles/assign")
    suspend fun assignRole(
        @Body request: AssignRoleRequest
    ): Response<Unit>

    @POST("api/roles/remove")
    suspend fun removeRole(
        @Body request: RemoveRoleRequest
    ): Response<Unit>
}