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

data class HomeUiState(
    val featuredProduct: Product? = null,
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All"
)

class HomeViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    val isUserLoggedIn: Boolean
        get() = userRepository.currentUser != null

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load featured product
            when (val featuredResult = productRepository.getFeaturedProduct()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(featuredProduct = featuredResult.data)
                }
                is Result.Error -> {
                    // Continue even if featured product fails
                }
                is Result.Loading -> {}
            }

            // Load all products
            when (val productsResult = productRepository.getAllProducts()) {
                is Result.Success -> {
                    allProducts = productsResult.data
                    _uiState.value = _uiState.value.copy(
                        products = allProducts,
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
    }

    fun filterByCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)

        val filtered = if (category == "All") {
            allProducts
        } else {
            allProducts.filter { it.category.equals(category, ignoreCase = true) }
        }

        _uiState.value = _uiState.value.copy(products = filtered)
    }
}