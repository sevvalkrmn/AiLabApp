package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GlobalLabStatsResponse(
    @SerializedName("currentOccupancyCount")
    val currentOccupancyCount: Int,

    @SerializedName("totalCapacity")
    val totalCapacity: Int,

    @SerializedName("peopleInside")
    val peopleInside: List<String>  // ✅ Kullanılmayacak ama model'de olmalı
)