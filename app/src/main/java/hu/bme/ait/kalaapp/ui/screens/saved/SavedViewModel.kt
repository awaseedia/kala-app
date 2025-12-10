package hu.bme.ait.kalaapp.ui.screens.saved

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

sealed class SavedUiState {
    object NotLoggedIn : SavedUiState()
    object Loading : SavedUiState()
    data class Success(val products: List<Product>) : SavedUiState()
    data class Error(val message: String) : SavedUiState()
}

class SavedViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow<SavedUiState>(SavedUiState.Loading)
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private val _isRemoving = MutableStateFlow<Set<String>>(emptySet())
    val isRemoving: StateFlow<Set<String>> = _isRemoving.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = userRepository.currentUser != null

    init {
//        loadSavedProducts()
    }

    fun loadSavedProducts() {
        if (!isUserLoggedIn) {
            _uiState.value = SavedUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            _uiState.value = SavedUiState.Loading

            // Get saved product IDs
            when (val savedResult = userRepository.getSavedProducts()) {
                is Result.Success -> {
                    val savedProductIds = savedResult.data

                    if (savedProductIds.isEmpty()) {
                        _uiState.value = SavedUiState.Success(emptyList())
                        return@launch
                    }

                    // Load full product details for each saved product
                    val products = mutableListOf<Product>()
                    var hasError = false

                    for (productId in savedProductIds) {
                        when (val productResult = productRepository.getProductById(productId)) {
                            is Result.Success -> {
                                products.add(productResult.data)
                            }
                            is Result.Error -> {
                                hasError = true
                            }
                            is Result.Loading -> {}
                        }
                    }

                    if (hasError && products.isEmpty()) {
                        _uiState.value = SavedUiState.Error("Failed to load saved products")
                    } else {
                        _uiState.value = SavedUiState.Success(products)
                    }
                }
                is Result.Error -> {
                    _uiState.value = SavedUiState.Error(
                        savedResult.exception.message ?: "Failed to load saved items"
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    fun removeFromSaved(productId: String) {
        viewModelScope.launch {
            // Add to removing set for UI feedback
            _isRemoving.value = _isRemoving.value + productId

            when (userRepository.removeFromFavorites(productId)) {
                is Result.Success -> {
                    // Remove from current list
                    val currentState = _uiState.value
                    if (currentState is SavedUiState.Success) {
                        _uiState.value = SavedUiState.Success(
                            currentState.products.filter { it.id != productId }
                        )
                    }
                }
                is Result.Error -> {
                    // Silently fail or show error
                }
                is Result.Loading -> {}
            }

            // Remove from removing set
            _isRemoving.value = _isRemoving.value - productId
        }
    }

    fun refresh() {
        loadSavedProducts()
    }
}