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

import com.ktun.ailabapp.domain.repository.IUserRepository
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.util.Logger

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
    private val userRepository: IUserRepository
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
            when (val userResult = userRepository.getAllUsers(1, 1000)) {
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
                        Logger.d("peopleInside: ${stats.peopleInside}", tag = "LabPeopleVM")
                        Logger.d("allUsers names: ${allUsers.map { it.fullName }}", tag = "LabPeopleVM")
                        val mappedPeople = stats.peopleInside.map { name ->
                            val normalizedName = name.trim().lowercase()
                            val user = allUsers.find { user ->
                                val full = user.fullName.trim().lowercase()
                                full == normalizedName ||
                                full.startsWith(normalizedName) ||
                                normalizedName.startsWith(full)
                            }
                            Logger.d("'$name' -> userId=${user?.id}", tag = "LabPeopleVM")
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

    var hasAnimated = false
        private set

    fun markAnimated() { hasAnimated = true }

    // ✅ YENİ: Zorla Çıkış
    fun forceCheckout(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            Logger.d("forceCheckout başlatıldı: userId=$userId", tag = "LabPeopleVM")
            when (val result = labStatsRepository.forceCheckout(userId = userId)) {
                is NetworkResult.Success -> {
                    Logger.d("forceCheckout başarılı, liste yenileniyor", tag = "LabPeopleVM")
                    loadLabPeople()
                }
                is NetworkResult.Error -> {
                    Logger.e("forceCheckout HATA: ${result.message}", tag = "LabPeopleVM")
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
