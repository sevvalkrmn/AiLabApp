package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ForceCheckoutRequest(
    @SerializedName("roomId") val roomId: String? = null,
    @SerializedName("userId") val userId: String? = null
)
