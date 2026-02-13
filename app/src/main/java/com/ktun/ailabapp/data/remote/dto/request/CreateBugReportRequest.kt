package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateBugReportRequest(
    @SerializedName("platform")
    val platform: Int = 2, // 2: Mobile

    @SerializedName("bugType")
    val bugType: Int, // 1: Görsel, 2: Fonksiyonel, 3: Performans, 4: Çökme, 5: Yetki, 99: Diğer

    @SerializedName("pageInfo")
    val pageInfo: String,

    @SerializedName("description")
    val description: String
)
