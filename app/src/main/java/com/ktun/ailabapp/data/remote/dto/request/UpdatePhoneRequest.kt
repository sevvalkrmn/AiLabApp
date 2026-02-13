package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdatePhoneRequest(
    @SerializedName("phoneNumber") val phoneNumber: String
)
