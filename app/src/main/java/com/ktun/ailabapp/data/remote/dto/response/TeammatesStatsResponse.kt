package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TeammatesStatsResponse(
    @SerializedName("teammatesInsideCount")
    val teammatesInsideCount: Int,

    @SerializedName("totalTeammatesCount")
    val totalTeammatesCount: Int
)