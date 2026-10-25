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

/**
 * ViewModel responsible for managing registration state, handling user UI intents,
 * and executing the email registration use case.
 *
 * @property registerWithEmailUseCase Use case for registering a new user with email, password, username, and display name.
 */
@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<RegisterUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<RegisterUiEffect> get() = _uiEffect.asSharedFlow()

    /**
     * Processes incoming [RegisterUiIntent] actions from the UI and updates state or triggers effects accordingly.
     *
     * @param intent The [RegisterUiIntent] to handle.
     */
    fun handleIntent(intent: RegisterUiIntent) = when(intent) {
        is RegisterUiIntent.OnEmailChanged -> _uiState.update { it.copy(email = intent.email) }
        is RegisterUiIntent.OnPasswordChanged -> _uiState.update {
            it.copy(password = intent.password, showPasswordsMismatch = false)
        }
        is RegisterUiIntent.OnConfirmPasswordChanged -> _uiState.update {
            it.copy(confirmPassword = intent.confirmPassword, showPasswordsMismatch = false)
        }
        is RegisterUiIntent.OnUsernameChanged -> _uiState.update { it.copy(username = intent.username) }
        is RegisterUiIntent.OnDisplayNameChanged -> _uiState.update { it.copy(displayName = intent.displayName) }
        is RegisterUiIntent.OnLoginNavigated -> emmitEffect(RegisterUiEffect.NavigateToLogin(null))
        is RegisterUiIntent.OnRegisterClicked -> {
            _uiState.update { it.copy(showErrors = true, showPasswordsMismatch = !it.passwordsMatch) }
            if (!_uiState.value.passwordsMatch) emmitEffect(RegisterUiEffect.ShowSnackBar("Passwords do not match."))
            else registerWithEmail()
        }
    }

    /**
     * Initiates the registration process with the provided email, password, username, and display name.
     */
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
                emmitEffect(RegisterUiEffect.NavigateToLogin(currentState.email))
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(RegisterUiEffect.ShowSnackBar(error.localizedMessage ?: "Error registering user."))
            }
        }
    }

    /**
     * Emits a one-off [RegisterUiEffect] to the UI.
     *
     * @param effect The [RegisterUiEffect] to emit.
     */
    private fun emmitEffect(effect: RegisterUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}