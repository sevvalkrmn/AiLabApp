package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RfidRegisterRequest(
    @SerializedName("userId") val userId: String
)
