package com.ktunailab.ailabapp.data.repository

import android.content.Context
import com.ktunailab.ailabapp.data.local.datastore.PreferencesManager
import com.ktunailab.ailabapp.data.remote.api.TaskApi
import com.ktunailab.ailabapp.data.remote.dto.request.CreateTaskRequest
import com.ktunailab.ailabapp.data.remote.dto.request.UpdateTaskRequest
import com.ktunailab.ailabapp.data.remote.dto.request.UpdateTaskStatusRequest
import com.ktunailab.ailabapp.data.remote.dto.response.TaskResponse
import com.ktunailab.ailabapp.data.remote.interceptor.AuthInterceptor
import com.ktunailab.ailabapp.data.remote.network.ApiConfig
import com.ktunailab.ailabapp.util.Constants
import com.ktunailab.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TaskRepository(private val context: Context) {

    private val taskApi: TaskApi by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val preferencesManager = PreferencesManager(context)
        val authInterceptor = AuthInterceptor(preferencesManager)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
    }

    /**
     * Projenin tüm görevlerini getir
     */
    suspend fun getProjectTasks(projectId: String): NetworkResult<List<TaskResponse>> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("TaskRepository", "Proje görevleri çekiliyor: $projectId")

                val response = taskApi.getProjectTasks(projectId)

                if (response.isSuccessful && response.body() != null) {
                    val paginatedResponse = response.body()!!
                    val tasks = paginatedResponse.items

                    android.util.Log.d("TaskRepository", "Görev sayısı: ${tasks.size}")

                    NetworkResult.Success(tasks)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("TaskRepository", """
                        Görevler Error:
                        Code: ${response.code()}
                        Error: $errorBody
                    """.trimIndent())

                    val errorMessage = when (response.code()) {
                        401 -> "Oturum süresi dolmuş."
                        403 -> "Bu projenin görevlerini görüntüleme yetkiniz yok."
                        404 -> "Proje bulunamadı."
                        else -> "Görevler yüklenemedi."
                    }
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskRepository", "Görevler Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Kullanıcının görevlerini getir
     */
    suspend fun getMyTasks(status: String? = null): NetworkResult<List<TaskResponse>> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("TaskRepository", "Kullanıcı görevleri çekiliyor. Status: $status")

                val response = taskApi.getMyTasks(status)

                if (response.isSuccessful && response.body() != null) {
                    val tasks = response.body()!!

                    android.util.Log.d("TaskRepository", "Görev sayısı: ${tasks.size}")

                    tasks.forEach { task ->
                        android.util.Log.d("TaskRepository", "Görev: ${task.title} - Status: ${task.status}")
                    }

                    NetworkResult.Success(tasks)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("TaskRepository", "My Tasks Error: $errorBody")

                    NetworkResult.Error("Görevler yüklenemedi.")
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskRepository", "My Tasks Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Görev durumunu güncelle
     */
    suspend fun updateTaskStatus(
        taskId: String,
        status: String
    ): NetworkResult<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("TaskRepository", "Görev durumu güncelleniyor: $taskId -> $status")

            val numericStatus = when (status) {
                "Todo" -> 0
                "InProgress" -> 1
                "Done" -> 2
                else -> 0
            }

            val request = UpdateTaskStatusRequest(numericStatus)
            val response = taskApi.updateTaskStatus(taskId, request)

            if (response.isSuccessful && response.body() != null) {
                val task = response.body()!!

                android.util.Log.d("TaskRepository", "Görev durumu güncellendi")

                if (status == "Done") {
                    android.util.Log.d("TaskRepository", "🎉 Görev tamamlandı! +10 puan kazanıldı!")
                }

                NetworkResult.Success(task)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("TaskRepository", "Durum güncelleme hatası: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz durum değeri."
                    403 -> "Bu görevi güncelleme yetkiniz yok."
                    404 -> "Görev bulunamadı."
                    else -> "Görev durumu güncellenemedi."
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Durum güncelleme exception", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}