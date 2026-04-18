package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.remote.dto.response.GlobalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.PersonalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.RoomResponse
import com.ktun.ailabapp.data.remote.dto.response.TeammatesStatsResponse
import com.ktun.ailabapp.util.NetworkResult

interface ILabStatsRepository {
    suspend fun getGlobalLabStats(): NetworkResult<GlobalLabStatsResponse>
    suspend fun getTeammatesStats(): NetworkResult<TeammatesStatsResponse>
    suspend fun getPersonalLabStats(): NetworkResult<PersonalLabStatsResponse>
    suspend fun getRooms(): NetworkResult<List<RoomResponse>>
    suspend fun getAccessMode(roomId: String): NetworkResult<Int>
    suspend fun updateAccessMode(roomId: String, mode: Int): NetworkResult<Unit>
    suspend fun forceCheckout(userId: String? = null, roomId: String? = null): NetworkResult<Unit>
}
