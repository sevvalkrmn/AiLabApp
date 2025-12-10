package com.ktun.ailabapp.data.model

data class HomeUiState(
    val userName: String = "Şevval",
    val currentDate: String = "Monday, June 25",
    val activeUsers: Int = 7,
    val totalCapacity: Int = 15,
    val lastLoginDate: String = "27.07.2004",
    val activeTeamMembers: String = "1 / 3",
    val leaderboardList: List<LeaderboardItem> = emptyList(),
    val isLoading: Boolean = false
)

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val points: Int,
    val profileImageUrl: String = ""
)

