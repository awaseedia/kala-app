package hu.bme.ait.kalaapp.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.Product
import hu.bme.ait.kalaapp.data.repository.ProductRepository
import hu.bme.ait.kalaapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isSavingFavorite: Boolean = false
)

class ProductDetailViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = userRepository.currentUser != null

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = productRepository.getProductById(productId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        product = result.data,
                        isLoading = false
                    )
                    // Check if product is saved
                    checkIfProductIsSaved(productId)
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

    private suspend fun checkIfProductIsSaved(productId: String) {
        if (!isUserLoggedIn) return

        when (val result = userRepository.getSavedProducts()) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isSaved = productId in result.data
                )
            }
            is Result.Error -> {
                // Silently fail - user just won't see saved status
            }
            is Result.Loading -> {}
        }
    }

    fun toggleFavorite(productId: String): String? {
        if (!isUserLoggedIn) {
            return "login_required"
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingFavorite = true)

            val result = if (_uiState.value.isSaved) {
                userRepository.removeFromFavorites(productId)
            } else {
                userRepository.addToFavorites(productId)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaved = !_uiState.value.isSaved,
                        isSavingFavorite = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isSavingFavorite = false)
                }
                is Result.Loading -> {}
            }
        }
        return null
    }
}