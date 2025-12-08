package hu.bme.ait.kalaapp.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.Brand
import hu.bme.ait.kalaapp.data.repository.BrandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MenuUiState(
    val brands: List<Brand> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MenuViewModel : ViewModel() {
    private val brandRepository = BrandRepository()

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadBrands()
    }

    fun loadBrands() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = brandRepository.getAllBrands()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        brands = result.data.sortedBy { it.name },
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }
                is Result.Loading -> {}
            }
        }
    }
}