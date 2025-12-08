package hu.bme.ait.kalaapp.ui.screens.brand

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.Brand
import hu.bme.ait.kalaapp.data.model.Product
import hu.bme.ait.kalaapp.data.repository.BrandRepository
import hu.bme.ait.kalaapp.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrandDetailUiState(
    val brand: Brand? = null,
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class BrandDetailViewModel : ViewModel() {
    private val brandRepository = BrandRepository()
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow(BrandDetailUiState())
    val uiState: StateFlow<BrandDetailUiState> = _uiState.asStateFlow()

    fun loadBrand(brandId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load brand details
            when (val brandResult = brandRepository.getBrandById(brandId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(brand = brandResult.data)

                    // Load brand's products
                    when (val productsResult = productRepository.getProductsByBrand(brandId)) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                products = productsResult.data,
                                isLoading = false
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = productsResult.exception.message
                            )
                        }
                        is Result.Loading -> {}
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = brandResult.exception.message
                    )
                }
                is Result.Loading -> {}
            }
        }
    }
}
