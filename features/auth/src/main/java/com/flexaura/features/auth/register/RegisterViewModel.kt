package com.flexaura.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<RegisterUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<RegisterUiEffect> get() = _uiEffect.asSharedFlow()

    fun handleIntent(intent: RegisterUiIntent) = when(intent) {
        is RegisterUiIntent.OnEmailChanged -> _uiState.update { it.copy(email = intent.email) }
        is RegisterUiIntent.OnPasswordChanged -> _uiState.update { it.copy(password = intent.password) }
        is RegisterUiIntent.OnUsernameChanged -> _uiState.update { it.copy(username = intent.username) }
        is RegisterUiIntent.OnDisplayNameChanged -> _uiState.update { it.copy(displayName = intent.displayName) }
        is RegisterUiIntent.OnLoginNavigated -> emmitEffect(RegisterUiEffect.NavigateToLogin)
        is RegisterUiIntent.OnRegisterClicked -> registerWithEmail()
    }

    private fun registerWithEmail() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            registerWithEmailUseCase(
                email = currentState.email,
                password = currentState.password,
                username = currentState.username,
                displayName = currentState.displayName
            )
            .onSuccess {
                emmitEffect(RegisterUiEffect.NavigateToHome)
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(RegisterUiEffect.ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
        }
    }

    private fun emmitEffect(effect: RegisterUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}