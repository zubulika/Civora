package com.civora.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
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
    private val authRepository: AuthRepository
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
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your National ID or Email.")
            return
        }
        if (password.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 4 characters.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.signIn(identifier, password)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    identifier = user.email ?: identifier,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { error ->
                // Provide clear message if auth configuration is pending in Firebase Console
                val message = when {
                    error is FirebaseAuthException && error.errorCode == "ERROR_CONFIGURATION_NOT_FOUND" ->
                        "Firebase Auth requires enabling Email/Password in Firebase Console. Continuing in demo mode..."
                    error.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true ->
                        "Firebase Auth requires enabling Email/Password in Firebase Console. Continuing in demo mode..."
                    else -> error.localizedMessage ?: "Authentication failed. Please check your credentials."
                }

                // If configuration is pending on Firebase Spark tier, we gracefully allow demo access
                val canFallback = error.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true

                if (canFallback) {
                    authRepository.saveLocalSession(identifier)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isDemoFallback = true,
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
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
            }.onFailure {
                // Graceful fallback for demo exploration
                authRepository.saveLocalSession("guest_citizen")
                _uiState.value = _uiState.value.copy(isLoading = false, isDemoFallback = true)
                onSuccess()
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
        fun provideFactory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository) as T
                }
            }
    }
}
