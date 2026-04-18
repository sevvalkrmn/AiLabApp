package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GlobalLabStatsResponse(
    @SerializedName("roomId")
    val roomId: String? = null,

    @SerializedName("currentOccupancyCount")
    val currentOccupancyCount: Int,

    @SerializedName("totalCapacity")
    val totalCapacity: Int,

    @SerializedName("peopleInside")
    val peopleInside: List<String>
)