package com.ktun.ailabapp.util

data class CacheEntry<T>(
    val data: T,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun isValid(ttlMs: Long): Boolean = System.currentTimeMillis() - timestamp < ttlMs
}
