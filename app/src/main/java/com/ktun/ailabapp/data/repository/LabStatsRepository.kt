package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.RoomsApi
import com.ktun.ailabapp.data.remote.dto.request.ForceCheckoutRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateAccessModeRequest
import com.ktun.ailabapp.data.remote.dto.response.GlobalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.PersonalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.RoomResponse
import com.ktun.ailabapp.data.remote.dto.response.TeammatesStatsResponse
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

interface LabStatsRepository {
    suspend fun getGlobalLabStats(): NetworkResult<GlobalLabStatsResponse>
    suspend fun getTeammatesStats(): NetworkResult<TeammatesStatsResponse>
    suspend fun getPersonalLabStats(): NetworkResult<PersonalLabStatsResponse>
    
    // Oda Erişimi
    suspend fun getRooms(): NetworkResult<List<RoomResponse>>
    suspend fun getAccessMode(roomId: String): NetworkResult<Int>
    suspend fun updateAccessMode(roomId: String, mode: Int): NetworkResult<Unit>
    
    // Zorla Çıkış
    suspend fun forceCheckout(userId: String? = null, roomId: String? = null): NetworkResult<Unit>
}

class LabStatsRepositoryImpl @Inject constructor(
    private val roomsApi: RoomsApi
) : LabStatsRepository {

    override suspend fun getGlobalLabStats(): NetworkResult<GlobalLabStatsResponse> {
        return try {
            val response = roomsApi.getGlobalLabStats()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(message = response.message() ?: "Bilinmeyen hata")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.localizedMessage ?: "Bağlantı hatası")
        }
    }

    override suspend fun getTeammatesStats(): NetworkResult<TeammatesStatsResponse> {
        return try {
            val response = roomsApi.getTeammatesStats()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(message = response.message() ?: "Bilinmeyen hata")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.localizedMessage ?: "Bağlantı hatası")
        }
    }

    override suspend fun getPersonalLabStats(): NetworkResult<PersonalLabStatsResponse> {
        return try {
            val response = roomsApi.getPersonalLabStats()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(message = response.message() ?: "Bilinmeyen hata")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.localizedMessage ?: "Bağlantı hatası")
        }
    }

    override suspend fun getRooms(): NetworkResult<List<RoomResponse>> {
        return try {
            val response = roomsApi.getRooms()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Odalar alınamadı: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Bağlantı hatası: ${e.message}")
        }
    }

    override suspend fun getAccessMode(roomId: String): NetworkResult<Int> {
        return try {
            val response = roomsApi.getAccessMode(roomId)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.mode)
            } else {
                NetworkResult.Error("Erişim modu alınamadı: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Bağlantı hatası: ${e.message}")
        }
    }

    override suspend fun updateAccessMode(roomId: String, mode: Int): NetworkResult<Unit> {
        return try {
            val request = UpdateAccessModeRequest(mode)
            val response = roomsApi.updateAccessMode(roomId, request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Erişim modu güncellenemedi")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Hata")
        }
    }

    override suspend fun forceCheckout(userId: String?, roomId: String?): NetworkResult<Unit> {
        return try {
            val request = ForceCheckoutRequest(userId = userId, roomId = roomId)
            val response = roomsApi.forceCheckout(request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Çıkış işlemi başarısız: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Hata: ${e.message}")
        }
    }
}
