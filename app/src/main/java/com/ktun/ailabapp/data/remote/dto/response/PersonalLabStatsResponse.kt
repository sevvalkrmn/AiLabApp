package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PersonalLabStatsResponse(
    @SerializedName("totalTimeSpent")
    val totalTimeSpent: String, // "12:30:45"

    @SerializedName("lastEntryDate")
    val lastEntryDate: String? // "2023-10-27T14:30:00Z" veya null
)