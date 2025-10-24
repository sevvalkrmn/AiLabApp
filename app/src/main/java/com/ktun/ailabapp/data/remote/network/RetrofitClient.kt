package com.ktun.ailabapp.data.remote.network

import android.content.Context
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.api.AuthApi
import com.ktun.ailabapp.data.remote.api.ProjectApi
import com.ktun.ailabapp.data.remote.api.TaskApi
import com.ktun.ailabapp.data.remote.interceptor.AuthInterceptor
import com.ktun.ailabapp.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    @Volatile
    private var retrofit: Retrofit? = null

    private fun getRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(context).also { retrofit = it }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        // Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Auth Interceptor
        val preferencesManager = PreferencesManager(context)
        val authInterceptor = AuthInterceptor(preferencesManager)

        // OkHttp Client
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        // Retrofit Instance
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // AuthApi instance
    fun getAuthApi(context: Context): AuthApi {
        return getRetrofit(context).create(AuthApi::class.java)
    }

    // ProjectApi instance - EKLE
    fun getProjectApi(context: Context): ProjectApi {
        return getRetrofit(context).create(ProjectApi::class.java)
    }

    // TaskApi instance - EKLE
    fun getTaskApi(context: Context): TaskApi {
        return getRetrofit(context).create(TaskApi::class.java)
    }
}