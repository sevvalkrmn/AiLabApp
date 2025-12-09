package com.ktunailab.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.response.PersonalLabStatsResponse
import com.ktunailab.ailabapp.data.remote.dto.response.GlobalLabStatsResponse
import com.ktunailab.ailabapp.data.remote.dto.response.TeammatesStatsResponse
import retrofit2.Response
import retrofit2.http.GET

interface RoomsApi {

    @GET("api/Rooms/stats/global")  // ✅ Yeni endpoint
    suspend fun getGlobalLabStats(): Response<GlobalLabStatsResponse>

    @GET("api/Rooms/stats/teammates")  // ✅ Aynı endpoint
    suspend fun getTeammatesStats(): Response<TeammatesStatsResponse>

    @GET("api/Rooms/stats/me")  // ✅ Aynı endpoint
    suspend fun getPersonalLabStats(): Response<PersonalLabStatsResponse>
}