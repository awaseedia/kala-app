package hu.bme.ait.kalaapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.Product
import hu.bme.ait.kalaapp.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<Product>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        // Cancel previous search job
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchState.value = SearchState.Initial
            return
        }

        // Debounce search - wait 500ms after user stops typing
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _searchState.value = SearchState.Loading

        when (val result = productRepository.searchProducts(query)) {
            is Result.Success -> {
                _searchState.value = SearchState.Success(result.data)
            }
            is Result.Error -> {
                _searchState.value = SearchState.Error(
                    result.exception.message ?: "Search failed"
                )
            }
            is Result.Loading -> {
                _searchState.value = SearchState.Loading
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = SearchState.Initial
    }
}