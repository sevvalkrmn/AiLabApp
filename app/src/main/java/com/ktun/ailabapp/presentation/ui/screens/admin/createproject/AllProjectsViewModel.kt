// presentation/ui/screens/admin/allprojects/AllProjectsViewModel.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.createproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllProjectsState())
    val state = _state.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = projectRepository.getMyProjects()) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            projects = result.data ?: emptyList()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> { /* Ignore */ }
            }
        }
    }
}

data class AllProjectsState(
    val projects: List<MyProjectsResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)