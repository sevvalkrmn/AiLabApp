package com.ktun.ailabapp.presentation.ui.screens.admin.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.domain.repository.ILabStatsRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.data.model.User

data class LabPeopleUiState(
    val peopleInside: List<LabPerson> = emptyList(),
    val currentOccupancy: Int = 0,
    val totalCapacity: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class LabPerson(
    val name: String,
    val id: String? = null
)

@HiltViewModel
class LabPeopleViewModel @Inject constructor(
    private val labStatsRepository: ILabStatsRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LabPeopleUiState())
    val uiState: StateFlow<LabPeopleUiState> = _uiState.asStateFlow()

    private var allUsers: List<User> = emptyList()
    private var roomId: String? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val userResult = userRepository.getAllUsers(1, 1000)) {
                is NetworkResult.Success -> allUsers = userResult.data ?: emptyList()
                is NetworkResult.Error -> {}
                is NetworkResult.Loading -> {}
            }

            loadLabPeople()
        }
    }

    fun loadLabPeople() {
        viewModelScope.launch {
            when (val result = labStatsRepository.getGlobalLabStats()) {
                is NetworkResult.Success -> {
                    result.data?.let { stats ->
                        if (stats.roomId != null) roomId = stats.roomId

                        val mappedPeople = stats.peopleInside.map { name ->
                            val normalizedName = name.trim().lowercase()
                            val user = allUsers.find { it.fullName.trim().lowercase() == normalizedName }
                                ?: allUsers.filter { user ->
                                    val full = user.fullName.trim().lowercase()
                                    full.startsWith("$normalizedName ") || normalizedName.startsWith("$full ")
                                }.minByOrNull { it.fullName.length }
                            LabPerson(name = name, id = user?.id)
                        }

                        _uiState.update {
                            it.copy(
                                peopleInside = mappedPeople,
                                currentOccupancy = stats.currentOccupancyCount,
                                totalCapacity = stats.totalCapacity,
                                isLoading = false
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }

    fun forceCheckout(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = labStatsRepository.forceCheckout(userId = userId, roomId = roomId)) {
                is NetworkResult.Success -> loadLabPeople()
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
