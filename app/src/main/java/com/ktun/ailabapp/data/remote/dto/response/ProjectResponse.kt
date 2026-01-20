// data/remote/dto/response/ProjectDetailResponse.kt

package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.ktun.ailabapp.data.model.UserProject

data class MyProjectsResponse(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("createdAt")
    val createdAt: String = "",

    @SerializedName("createdBy")
    val createdBy: String = "",

    @SerializedName("createdByName")
    val createdByName: String = "",

    // ✅ YENİ - Backend döndürüyor
    @SerializedName("memberCount")
    val memberCount: Int = 0,

    @SerializedName("taskCount")
    val taskCount: Int = 0,

    @SerializedName("captainNames")
    val captainNames: List<String> = emptyList(),

    @SerializedName("userRole")
    val userRole: String? = null

)
// GET /api/projects/{id} için detaylı proje
data class ProjectDetailResponse(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("createdAt")
    val createdAt: String = "",

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("taskStatistics")
    val taskStatistics: TaskStatistics = TaskStatistics(),

    @SerializedName("captains")
    val captains: List<ProjectMember> = emptyList(),

    @SerializedName("members")
    val members: List<ProjectMember> = emptyList()
)

data class TaskStatistics(
    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("todo")
    val todo: Int = 0,

    @SerializedName("inProgress")
    val inProgress: Int = 0,

    @SerializedName("done")
    val done: Int = 0
)

data class ProjectMember(
    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("fullName")
    val fullName: String = "İsimsiz",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("role")
    val role: String = "Member"
)

// ✅ MAPPING FONKSIYONLARI

/**
 * ProjectDetailResponse → UserProject mapping
 * Kullanıcının bu projedeki rolünü belirler
 */
fun ProjectDetailResponse.toUserProject(userId: String): UserProject {
    // Kullanıcı captain'lar listesinde mi?
    val isCaptain = captains.any { it.userId == userId }

    // Kullanıcı members listesinde mi?
    val isMember = members.any { it.userId == userId }

    val role = when {
        isCaptain -> "Captain"
        isMember -> "Member"
        else -> null
    }

    return UserProject(
        id = id,
        name = name,
        role = role
    )
}

/**
 * MyProjectsResponse → UserProject mapping
 * Backend zaten userRole döndürüyor
 */
fun MyProjectsResponse.toUserProject(): UserProject {
    return UserProject(
        id = id,
        name = name,
        role = userRole
    )
}