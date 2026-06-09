package com.flexaura.features.auth.login

sealed interface LoginUiIntent {
    data class OnEmailChanged(val email: String): LoginUiIntent
    data class OnPasswordChanged(val password: String): LoginUiIntent
    data object OnLoginClicked: LoginUiIntent
    data object OnGoogleLoginClicked: LoginUiIntent
    data object OnRegisterNavigated: LoginUiIntent
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginUiEffect {
    data object NavigateToRegister: LoginUiEffect
    data object NavigateToHome: LoginUiEffect
    data object LaunchGoogleSignIn: LoginUiEffect
    data class ShowSnackBar(val message: String): LoginUiEffect
}