package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.remote.api.TaskApi
import com.ktun.ailabapp.data.remote.dto.request.CreateTaskRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateTaskStatusRequest
import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.toTaskHistory
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.util.CacheEntry
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskApi: TaskApi
) : ITaskRepository {

    private companion object {
        const val MY_TASKS_TTL_MS = 2 * 60 * 1000L
    }

    private val myTasksCache = ConcurrentHashMap<String, CacheEntry<List<TaskResponse>>>()

    fun clearCache() {
        myTasksCache.clear()
    }

    /**
     * Projenin tüm görevlerini getir
     */
    override suspend fun getProjectTasks(projectId: String): NetworkResult<List<TaskResponse>> =
        withContext(Dispatchers.IO) {
            try {
                Logger.d( "Proje görevleri çekiliyor: $projectId")

                val response = taskApi.getProjectTasks(projectId)

                if (response.isSuccessful && response.body() != null) {
                    val paginatedResponse = response.body()!!
                    val tasks = paginatedResponse.items

                    Logger.d( "Görev sayısı: ${tasks.size}")

                    NetworkResult.Success(tasks)
                } else {
                    val errorBody = response.errorBody()?.string()

                    Logger.e( """
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
                Logger.e( "Görevler Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Kullanıcının görevlerini getir
     */
    override suspend fun getMyTasks(status: Int?): NetworkResult<List<TaskResponse>> =
        withContext(Dispatchers.IO) {
            val cacheKey = status?.toString() ?: "all"
            myTasksCache[cacheKey]?.let { if (it.isValid(MY_TASKS_TTL_MS)) return@withContext NetworkResult.Success(it.data) }

            try {
                Logger.d( "Kullanıcı görevleri çekiliyor. Status: $status")

                val response = taskApi.getMyTasks(status)

                if (response.isSuccessful && response.body() != null) {
                    val tasks = response.body()!!
                    myTasksCache[cacheKey] = CacheEntry(tasks)

                    Logger.d( "Görev sayısı: ${tasks.size}")

                    tasks.forEach { task ->
                        Logger.d( "Görev: ${task.title} - Status: ${task.status}")
                    }

                    NetworkResult.Success(tasks)
                } else {
                    val errorBody = response.errorBody()?.string()

                    Logger.e( "My Tasks Error: $errorBody")

                    NetworkResult.Error("Görevler yüklenemedi.")
                }
            } catch (e: Exception) {
                Logger.e( "My Tasks Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    override suspend fun getTaskDetail(taskId: String): NetworkResult<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            val response = taskApi.getTaskDetail(taskId)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Görev detayı alınamadı")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        projectId: String,
        assigneeId: String?,
        dueDate: String?
    ): NetworkResult<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreateTaskRequest(
                title = title,
                description = description,
                projectId = projectId,
                assigneeId = assigneeId,
                dueDate = dueDate
            )

            val response = taskApi.createTask(request)

            if (response.isSuccessful && response.body() != null) {
                myTasksCache.clear()
                NetworkResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                NetworkResult.Error("Görev oluşturulamadı: $errorBody")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    override suspend fun deleteTask(taskId: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = taskApi.deleteTask(taskId)

            if (response.isSuccessful) {
                myTasksCache.clear()
                NetworkResult.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                NetworkResult.Error("Görev silinemedi: $errorBody")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Görev durumunu güncelle
     */
    override suspend fun updateTaskStatus(
        taskId: String,
        status: String
    ): NetworkResult<TaskResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "Görev durumu güncelleniyor: $taskId -> $status")

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
                myTasksCache.clear()

                Logger.d( "Görev durumu güncellendi")

                if (status == "Done") {
                    Logger.d( "🎉 Görev tamamlandı! +10 puan kazanıldı!")
                }

                NetworkResult.Success(task)
            } else {
                val errorBody = response.errorBody()?.string()

                Logger.e( "Durum güncelleme hatası: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz durum değeri."
                    403 -> "Bu görevi güncelleme yetkiniz yok."
                    404 -> "Görev bulunamadı."
                    else -> "Görev durumu güncellenemedi."
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Logger.e( "Durum güncelleme exception", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    override suspend fun getUserTaskHistory(userId: String): NetworkResult<List<TaskHistory>> =
        withContext(Dispatchers.IO) {
            try {
                val response = taskApi.getUserTaskHistory(userId)
                if (response.isSuccessful) {
                    val tasks = response.body()?.map { it.toTaskHistory() } ?: emptyList()
                    NetworkResult.Success(tasks)
                } else {
                    NetworkResult.Error("Görev geçmişi yüklenemedi: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }
}