package com.ktun.ailabapp.data.remote.api

import com.ktun.ailabapp.data.remote.dto.response.PersonalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.GlobalLabStatsResponse
import com.ktun.ailabapp.data.remote.dto.response.TeammatesStatsResponse
import retrofit2.Response
import retrofit2.http.GET

import com.ktun.ailabapp.data.remote.dto.request.UpdateAccessModeRequest // ✅ Import
import com.ktun.ailabapp.data.remote.dto.response.RoomResponse // ✅ Import
import retrofit2.http.Body // ✅ Import
import retrofit2.http.PUT // ✅ Import
import retrofit2.http.Path // ✅ Import

import com.ktun.ailabapp.data.remote.dto.response.AccessModeResponse // ✅ Import

import com.ktun.ailabapp.data.remote.dto.request.ForceCheckoutRequest // ✅ Import
import retrofit2.http.POST // ✅ Import

interface RoomsApi {

    @POST("api/Rooms/force-checkout")
    suspend fun forceCheckout(@Body request: ForceCheckoutRequest): Response<Any>

    @GET("api/Rooms")
// ...
    suspend fun getRooms(): Response<List<RoomResponse>>

    @GET("api/Rooms/{roomId}/access-mode")
    suspend fun getAccessMode(@Path("roomId") roomId: String): Response<AccessModeResponse> // ✅ DTO Kullanımı

    @PUT("api/Rooms/{roomId}/access-mode")
    suspend fun updateAccessMode(
        @Path("roomId") roomId: String,
        @Body request: UpdateAccessModeRequest
    ): Response<Unit>

    @GET("api/Rooms/stats/global")  // ✅ Yeni endpoint
// ...
    suspend fun getGlobalLabStats(): Response<GlobalLabStatsResponse>

    @GET("api/Rooms/stats/teammates")  // ✅ Aynı endpoint
    suspend fun getTeammatesStats(): Response<TeammatesStatsResponse>

    @GET("api/Rooms/stats/me")  // ✅ Aynı endpoint
    suspend fun getPersonalLabStats(): Response<PersonalLabStatsResponse>
}