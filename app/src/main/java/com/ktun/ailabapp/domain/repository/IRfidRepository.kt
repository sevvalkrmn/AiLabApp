package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.util.NetworkResult

interface IRfidRepository {
    suspend fun startRegistration(userId: String): NetworkResult<Any>
    suspend fun checkStatus(): NetworkResult<String>
}
