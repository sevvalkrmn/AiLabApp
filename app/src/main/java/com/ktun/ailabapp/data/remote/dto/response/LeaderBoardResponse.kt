package com.ktun.ailabapp.data.remote.dto.response
import com.google.gson.annotations.SerializedName

data class LeaderboardUserResponse(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("totalScore")
    val totalScore: Int,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)

// List olarak gelecek
typealias LeaderboardResponse = List<LeaderboardUserResponse>