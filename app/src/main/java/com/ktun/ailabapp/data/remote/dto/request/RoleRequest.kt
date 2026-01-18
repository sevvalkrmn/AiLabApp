// data/remote/dto/request/RoleRequest.kt

package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class AssignRoleRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("roleName") val roleName: String
)

data class RemoveRoleRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("roleName") val roleName: String
)