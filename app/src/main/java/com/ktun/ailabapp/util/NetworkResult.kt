package com.ktun.ailabapp.util

sealed class NetworkResult<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error(message: String) : NetworkResult<Nothing>(null, message)  // ✅ Generic yok
    class Loading<T> : NetworkResult<T>()
}