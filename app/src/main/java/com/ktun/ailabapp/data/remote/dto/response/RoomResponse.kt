package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("accessMode") val accessMode: Int // 0 or 1
)
