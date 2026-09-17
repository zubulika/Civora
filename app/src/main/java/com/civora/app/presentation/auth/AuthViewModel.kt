package com.civora.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.data.repository.AuthRepository
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val identifier: String = "",
    val isDemoFallback: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Observe currentUser
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        identifier = user.email ?: user.uid
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isAuthenticated = false)
                }
            }
        }
    }

    fun login(identifier: String, password: String, onSuccess: () -> Unit) {
        if (identifier.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your National ID or username.")
            return
        }
        if (password.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 4 characters.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.signIn(identifier, password)
            result.onSuccess { profile ->
                userRepository?.setCurrentUser(profile)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    identifier = profile.nationalId,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Invalid credentials. Please verify your National ID and password."
                )
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.signInAnonymously()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Guest login is currently unavailable."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    companion object {
        fun provideFactory(
            authRepository: AuthRepository,
            userRepository: UserRepository? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository, userRepository) as T
                }
            }
    }
}
