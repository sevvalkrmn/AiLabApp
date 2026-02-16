package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.ElectricityApi
import com.ktun.ailabapp.data.remote.dto.request.ElectricityControlRequest
import com.ktun.ailabapp.data.remote.dto.response.ElectricityControlResponse
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ElectricityRepository @Inject constructor(
    private val electricityApi: ElectricityApi
) {
    suspend fun controlDevice(deviceId: String, turnOn: Boolean): NetworkResult<ElectricityControlResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = ElectricityControlRequest(deviceId = deviceId, turnOn = turnOn)
                val response = electricityApi.controlDevice(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        NetworkResult.Success(body)
                    } else {
                        NetworkResult.Error("Yanıt boş döndü")
                    }
                } else {
                    NetworkResult.Error("İşlem başarısız: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Bağlantı hatası: ${e.message}")
            }
        }
}
