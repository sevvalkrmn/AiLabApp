package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateAccessModeRequest(
    @SerializedName("mode") val mode: Int // 0 for Admin Only, 1 for All Members
)
