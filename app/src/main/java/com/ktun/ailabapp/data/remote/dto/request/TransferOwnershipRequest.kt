package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class TransferOwnershipRequest(
    @SerializedName("currentCaptainId") val currentCaptainId: String,
    @SerializedName("newCaptainId") val newCaptainId: String
)
