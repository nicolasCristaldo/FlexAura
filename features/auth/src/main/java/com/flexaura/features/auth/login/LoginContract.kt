package com.flexaura.features.auth.login

sealed interface LoginUiIntent {
    data class OnEmailChanged(val email: String): LoginUiIntent
    data class OnPasswordChanged(val password: String): LoginUiIntent
    object OnLoginClicked: LoginUiIntent
    object OnGoogleLoginClicked: LoginUiIntent
    object OnRegisterNavigated: LoginUiIntent
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginUiEffect {
    object NavigateToRegister: LoginUiEffect
    object NavigateToHome: LoginUiEffect
    data class ShowSnackBar(val message: String): LoginUiEffect
}