// data/remote/dto/request/AdjustScoreRequest.kt

package com.ktun.ailabapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class AdjustScoreRequest(
    @SerializedName("amount") val amount: Double, // ✅ scoreChange → amount
    @SerializedName("reason") val reason: String // ✅ reason zorunlu
)