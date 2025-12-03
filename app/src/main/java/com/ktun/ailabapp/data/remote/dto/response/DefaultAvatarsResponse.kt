package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class DefaultAvatarsResponse(
    @SerializedName("avatarUrls")
    val avatarUrls: List<String>
)