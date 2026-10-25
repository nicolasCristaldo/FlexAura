package com.flexaura.features.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.flexaura.data.domain.usecase.auth.LoginWithEmailUseCase
import com.flexaura.data.domain.usecase.auth.LoginWithGoogleUseCase
import com.flexaura.features.auth.navigation.LoginRouteDestination
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
 * ViewModel responsible for managing authentication state, handling user UI intents,
 * and executing email and Google login use cases.
 *
 * @property savedStateHandle Handle to saved state parameters, used to extract navigation route arguments.
 * @property loginWithEmailUseCase Use case for authenticating users via email and password.
 * @property loginWithGoogleUseCase Use case for authenticating users via Google ID tokens.
 */
@HiltViewModel
internal class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
): ViewModel() {
    private val navArgs = savedStateHandle.toRoute<LoginRouteDestination>()

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(
        LoginUiState(
            email = navArgs.email ?: "",
            showVerificationDialog = navArgs.email != null
        )
    )
    val uiState: StateFlow<LoginUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<LoginUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<LoginUiEffect> get() = _uiEffect.asSharedFlow()

    /**
     * Processes incoming [LoginUiIntent] actions from the UI and updates state or triggers effects accordingly.
     *
     * @param intent The [LoginUiIntent] to handle.
     */
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
        is LoginUiIntent.OnDismissVerificationDialog -> _uiState.update { it.copy(showVerificationDialog = false) }
    }

    /**
     * Initiates the authentication process with the provided email and password.
     */
    private fun loginWithEmail() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            loginWithEmailUseCase(email = currentState.email, password = currentState.password)
                .onSuccess {
                    emitEffect(LoginUiEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Unable to sign in.") }
                    _uiEffect.emit(LoginUiEffect.ShowSnackBar(error.localizedMessage ?: "Unable to sign in."))
                }
        }
    }

    /**
     * Initiates the Google authentication process.
     */
    private fun loginWithGoogle() {
        _uiState.update { it.copy(isLoading = true) }
        emitEffect(LoginUiEffect.LaunchGoogleSignIn)
    }

    /**
     * Handles the receipt of a Google ID token following a successful Google authentication flow.
     *
     * @param token The Google ID token string.
     */
    fun onGoogleTokenReceived(token: String) = viewModelScope.launch {
        loginWithGoogleUseCase(token)
            .onSuccess {
                _uiEffect.emit(LoginUiEffect.NavigateToHome)
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(LoginUiEffect.ShowSnackBar(error.localizedMessage ?: "Google sign in failed."))
            }
    }

    /**
     * Resets the loading state when the Google sign-in flow is cancelled by the user.
     */
    fun onGoogleSignInCancelled() {
        _uiState.update { it.copy(isLoading = false) }
    }

    /**
     * Emits a one-off [LoginUiEffect] to the UI.
     *
     * @param effect The [LoginUiEffect] to emit.
     */
    private fun emitEffect(effect: LoginUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}