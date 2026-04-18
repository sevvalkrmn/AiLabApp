package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.RfidApi
import com.ktun.ailabapp.data.remote.dto.request.RfidRegisterRequest
import com.ktun.ailabapp.domain.repository.IRfidRepository
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RfidRepository @Inject constructor(
    private val rfidApi: RfidApi,
    private val authManager: FirebaseAuthManager
) : IRfidRepository {
    override suspend fun startRegistration(userId: String): NetworkResult<Any> = withContext(Dispatchers.IO) {
        if (userId.isBlank()) {
            return@withContext NetworkResult.Error("Kullanıcı ID boş, RFID kayıt başlatılamaz")
        }

        try {
            val token = authManager.getIdToken()
            Logger.d("RFID kayıt başlatılıyor - userId: $userId, token: ${if (token != null) "VAR" else "YOK"}", tag = "RfidRepository")

            val request = RfidRegisterRequest(userId = userId, token = token)
            val response = rfidApi.startRegistration(request)

            if (response.isSuccessful) {
                Logger.d("RFID kayıt başarılı - userId: $userId, response: ${response.body()}", tag = "RfidRepository")
                NetworkResult.Success(response.body() ?: Any())
            } else {
                Logger.e("RFID kayıt başarısız - code: ${response.code()}, body: ${response.errorBody()?.string()}", tag = "RfidRepository")
                NetworkResult.Error("RFID Kayıt Başlatılamadı: ${response.code()}")
            }
        } catch (e: Exception) {
            Logger.e("RFID bağlantı hatası: ${e.message}", throwable = e, tag = "RfidRepository")
            NetworkResult.Error("Bağlantı Hatası: ${e.message}")
        }
    }

    override suspend fun checkStatus(): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = rfidApi.getStatus()
            if (response.isSuccessful) {
                val mode = response.body()?.get("mode") as? String ?: "unknown"
                NetworkResult.Success(mode)
            } else {
                NetworkResult.Error("Status alınamadı: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Bağlantı hatası: ${e.message}")
        }
    }
}
