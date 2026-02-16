package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ElectricityControlRequest(
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("turnOn") val turnOn: Boolean
)
