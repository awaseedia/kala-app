package hu.bme.ait.kalaapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.User
import hu.bme.ait.kalaapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object NotLoggedIn : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User, val savedItemsCount: Int) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val currentUser = userRepository.currentUser

        if (currentUser == null) {
            _uiState.value = ProfileUiState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            when (val result = userRepository.getUserData(currentUser.uid)) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState.Success(
                        user = result.data,
                        savedItemsCount = result.data.savedProductIds.size
                    )
                }
                is Result.Error -> {
                    _uiState.value = ProfileUiState.Error(
                        result.exception.message ?: "Failed to load profile"
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    fun signOut() {
        userRepository.signOut()
        _uiState.value = ProfileUiState.NotLoggedIn
    }

    fun refresh() {
        loadProfile()
    }
}