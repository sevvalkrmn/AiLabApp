package com.ktun.ailabapp.data.model

import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse

data class ProjectDetailUiState(
    val project: ProjectDetailResponse? = null,
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)