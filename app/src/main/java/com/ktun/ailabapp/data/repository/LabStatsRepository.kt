package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.dto.response.PersonalLabStatsResponse
import com.ktun.ailabapp.data.remote.api.RoomsApi
import com.ktun.ailabapp.data.remote.dto.response.GlobalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.TeammatesStatsResponse
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

interface LabStatsRepository {
    suspend fun getGlobalLabStats(): NetworkResult<GlobalLabStatsResponse>  // ✅ Global
    suspend fun getTeammatesStats(): NetworkResult<TeammatesStatsResponse>  // ✅ Teammates
    suspend fun getPersonalLabStats(): NetworkResult<PersonalLabStatsResponse>  // ✅ Personal
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
}