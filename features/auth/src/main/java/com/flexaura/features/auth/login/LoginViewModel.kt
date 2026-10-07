package com.flexaura.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.auth.LoginWithEmailUseCase
import com.flexaura.data.domain.usecase.auth.LoginWithGoogleUseCase
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
internal class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<LoginUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<LoginUiEffect> get() = _uiEffect.asSharedFlow()

    fun handleIntent(intent: LoginUiIntent) = when (intent) {
        is LoginUiIntent.OnEmailChanged -> _uiState.update { it.copy(email = intent.email) }
        is LoginUiIntent.OnPasswordChanged -> _uiState.update { it.copy(password = intent.password) }
        LoginUiIntent.OnShowPasswordChanged -> _uiState.update { it.copy(showPassword = !it.showPassword) }
        is LoginUiIntent.OnLoginClicked -> {
            _uiState.update { it.copy(showErrors = true) }
            loginWithEmail()
        }
        is LoginUiIntent.OnGoogleLoginClicked -> loginWithGoogle()
        is LoginUiIntent.OnRegisterNavigated -> emitEffect(LoginUiEffect.NavigateToRegister)
    }

    private fun loginWithEmail() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            loginWithEmailUseCase(email = currentState.email, password = currentState.password)
                .onSuccess {
                    emitEffect(LoginUiEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                    _uiEffect.emit(LoginUiEffect.ShowSnackBar(error.localizedMessage ?: "Unknown error"))
                }
        }
    }

    private fun loginWithGoogle() {
        _uiState.update { it.copy(isLoading = true) }
        emitEffect(LoginUiEffect.LaunchGoogleSignIn)
    }

    fun onGoogleTokenReceived(token: String) = viewModelScope.launch {
        loginWithGoogleUseCase(token)
            .onSuccess {
                _uiEffect.emit(LoginUiEffect.NavigateToHome)
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(LoginUiEffect.ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    fun onGoogleSignInCancelled() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun emitEffect(effect: LoginUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}