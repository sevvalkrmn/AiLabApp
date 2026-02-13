package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.RfidApi
import com.ktun.ailabapp.data.remote.dto.request.RfidRegisterRequest
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RfidRepository @Inject constructor(
    private val rfidApi: RfidApi
) {
    suspend fun startRegistration(userId: String): NetworkResult<Any> = withContext(Dispatchers.IO) {
        try {
            val request = RfidRegisterRequest(userId = userId)
            val response = rfidApi.startRegistration(request)

            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: Any())
            } else {
                NetworkResult.Error("RFID Kayıt Başlatılamadı: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Bağlantı Hatası: ${e.message}")
        }
    }
}
