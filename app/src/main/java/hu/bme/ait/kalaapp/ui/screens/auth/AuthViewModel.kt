package hu.bme.ait.kalaapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = userRepository.currentUser

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = userRepository.signIn(email, password)) {
                is Result.Success -> {
                    _authState.value = AuthState.Success(result.data)
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Sign in failed"
                    )
                }
                is Result.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = userRepository.signUp(email, password, displayName)) {
                is Result.Success -> {
                    _authState.value = AuthState.Success(result.data)
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Sign up failed"
                    )
                }
                is Result.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}