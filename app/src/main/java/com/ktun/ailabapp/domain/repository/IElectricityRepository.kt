package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.remote.dto.response.ElectricityControlResponse
import com.ktun.ailabapp.util.NetworkResult

interface IElectricityRepository {
    suspend fun controlDevice(deviceId: String, turnOn: Boolean): NetworkResult<ElectricityControlResponse>
}
