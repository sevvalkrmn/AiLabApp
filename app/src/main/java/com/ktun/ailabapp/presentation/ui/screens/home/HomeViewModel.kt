package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import com.ktun.ailabapp.data.model.HomeUiState
import com.ktun.ailabapp.data.repository.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = _uiState.value.copy(
            currentDate = getCurrentDate(),
            leaderboardList = SampleData.leaderboardList
        )
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH)
        return dateFormat.format(Date())
    }
}