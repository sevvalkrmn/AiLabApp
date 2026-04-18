package com.ktun.ailabapp.domain.repository

import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.util.NetworkResult

interface ITaskRepository {
    suspend fun getMyTasks(status: Int?): NetworkResult<List<TaskResponse>>
    suspend fun getProjectTasks(projectId: String): NetworkResult<List<TaskResponse>>
    suspend fun getTaskDetail(taskId: String): NetworkResult<TaskResponse>
    suspend fun createTask(title: String, description: String?, projectId: String, assigneeId: String?, dueDate: String?): NetworkResult<TaskResponse>
    suspend fun deleteTask(taskId: String): NetworkResult<Unit>
    suspend fun updateTaskStatus(taskId: String, status: String): NetworkResult<TaskResponse>
    suspend fun getUserTaskHistory(userId: String): NetworkResult<List<TaskHistory>>
}
