package com.flexaura.features.auth.register

sealed interface RegisterUiIntent {
    data class OnEmailChanged(val email: String): RegisterUiIntent
    data class OnPasswordChanged(val password: String): RegisterUiIntent
    data class OnUsernameChanged(val username: String): RegisterUiIntent
    data class OnDisplayNameChanged(val displayName: String): RegisterUiIntent
    data object OnRegisterClicked: RegisterUiIntent
    data object OnLoginNavigated: RegisterUiIntent
}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface RegisterUiEffect {
    data object NavigateToLogin: RegisterUiEffect
    data object NavigateToHome: RegisterUiEffect
    data class ShowSnackBar(val message: String): RegisterUiEffect
}