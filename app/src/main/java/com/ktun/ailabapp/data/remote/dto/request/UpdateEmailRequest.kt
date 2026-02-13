package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateEmailRequest(
    @SerializedName("newEmail") val newEmail: String
)
