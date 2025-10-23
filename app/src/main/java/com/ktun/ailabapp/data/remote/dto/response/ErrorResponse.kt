package com.ktun.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

// Backend'den dönen hata yapısı
// Örnek JSON:
// {
//   "success": false,
//   "message": "Bu email adresi zaten kayıtlı",
//   "errors": {
//     "email": ["Email kullanımda"]
//   }
// }

data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String?,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,

    @SerializedName("code")
    val code: Int? = null
)