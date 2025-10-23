package com.ktun.ailabapp.data.remote.network

object ApiConfig {

    // 🔧 Backend URL - Arkadaşınızdan alacağınız gerçek URL'i yazın
    const val BASE_URL = "https://api.ailab.org.tr/"

    // API Endpoints
    object Endpoints {
        const val REGISTER = "api/auth/register"
        const val LOGIN = "api/auth/login"
        const val LOGOUT = "api/auth/logout"
        const val GET_PROFILE = "api/user/profile"
        const val UPDATE_PROFILE = "api/user/profile"
        const val CHANGE_PASSWORD = "api/user/change-password"
    }

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
}