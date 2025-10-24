package com.ktunailab.ailabapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    @SerializedName("items")
    val items: List<T> = emptyList(),

    @SerializedName("totalCount")
    val totalCount: Int = 0,

    @SerializedName("pageNumber")
    val pageNumber: Int = 1,

    @SerializedName("pageSize")
    val pageSize: Int = 20,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("hasPrevious")
    val hasPrevious: Boolean = false,

    @SerializedName("hasNext")
    val hasNext: Boolean = false
)