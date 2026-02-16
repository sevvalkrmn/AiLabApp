package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ElectricityControlResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("isOn") val isOn: Boolean
)
