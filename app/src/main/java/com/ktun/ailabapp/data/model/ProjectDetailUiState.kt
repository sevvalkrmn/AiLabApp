package com.ktunailab.ailabapp.data.model

import com.ktunailab.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktunailab.ailabapp.data.remote.dto.response.TaskResponse

data class ProjectDetailUiState(
    val project: ProjectDetailResponse? = null,
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)