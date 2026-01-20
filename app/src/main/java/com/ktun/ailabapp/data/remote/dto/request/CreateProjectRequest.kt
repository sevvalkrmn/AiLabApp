// data/remote/dto/request/CreateProjectRequest.kt

package com.ktun.ailabapp.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String? = null,
    val captainUserId: String
)