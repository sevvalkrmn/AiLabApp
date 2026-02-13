package com.ktun.ailabapp.presentation.ui.screens.admin.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.repository.LabStatsRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ktun.ailabapp.data.repository.UserRepository // ✅ Import
import com.ktun.ailabapp.data.model.User // ✅ Import

data class LabPeopleUiState(
    val peopleInside: List<LabPerson> = emptyList(), // ✅ String -> LabPerson
    val currentOccupancy: Int = 0,
    val totalCapacity: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class LabPerson(
    val name: String,
    val id: String? = null // ID bulunamayabilir
)

@HiltViewModel
class LabPeopleViewModel @Inject constructor(
    private val labStatsRepository: LabStatsRepository,
    private val userRepository: UserRepository // ✅ Inject
) : ViewModel() {

    private val _uiState = MutableStateFlow(LabPeopleUiState())
    val uiState: StateFlow<LabPeopleUiState> = _uiState.asStateFlow()

    private var allUsers: List<User> = emptyList()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // 1. Önce tüm kullanıcıları çek (ID eşleşmesi için)
            when (val userResult = userRepository.getAllUsers(pageSize = 1000)) { // Büyük bir sayfa boyutu
                is NetworkResult.Success -> {
                    allUsers = userResult.data ?: emptyList()
                }
                is NetworkResult.Error -> {
                    // Kullanıcılar çekilemezse sadece isimlerle devam et
                }
                is NetworkResult.Loading -> {}
            }

            // 2. Lab istatistiklerini çek
            loadLabPeople()
        }
    }

    fun loadLabPeople() {
        viewModelScope.launch {
            // _uiState.update { it.copy(isLoading = true) } // Zaten loadData yapıyor

            when (val result = labStatsRepository.getGlobalLabStats()) {
                is NetworkResult.Success -> {
                    result.data?.let { stats ->
                        val mappedPeople = stats.peopleInside.map { name ->
                            val user = allUsers.find { it.fullName.equals(name, ignoreCase = true) }
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
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // ✅ YENİ: Zorla Çıkış
    fun forceCheckout(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = labStatsRepository.forceCheckout(userId = userId)) {
                is NetworkResult.Success -> {
                    loadLabPeople() // Listeyi yenile
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message 
                        ) 
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
