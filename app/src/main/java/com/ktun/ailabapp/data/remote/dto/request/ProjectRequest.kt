package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

/**
 * POST /api/projects için
 * Yeni proje oluşturma
 */
data class CreateProjectRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?
)

/**
 * PUT /api/projects/{id} için
 * Proje güncelleme
 */
data class UpdateProjectRequest(
    @SerializedName("name")
    val name: String?,

    @SerializedName("description")
    val description: String?
)

/**
 * POST /api/projects/{id}/members için
 * Projeye üye ekleme
 */
data class AddMemberRequest(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("role")
    val role: String  // "Captain" veya "Member"
)

/**
 * PUT /api/projects/{id}/members/{userId}/role için
 * Üye rolü değiştirme
 */
data class UpdateMemberRoleRequest(
    @SerializedName("role")
    val role: String  // "Captain" veya "Member"
)