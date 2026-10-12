package com.flexaura.features.auth.register

internal sealed interface RegisterUiIntent {
    data class OnEmailChanged(val email: String): RegisterUiIntent
    data class OnPasswordChanged(val password: String): RegisterUiIntent
    data class OnConfirmPasswordChanged(val confirmPassword: String): RegisterUiIntent
    data class OnUsernameChanged(val username: String): RegisterUiIntent
    data class OnDisplayNameChanged(val displayName: String): RegisterUiIntent
    data object OnRegisterClicked: RegisterUiIntent
    data object OnLoginNavigated: RegisterUiIntent
}

internal data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val username: String = "",
    val displayName: String = "",
    val showErrors: Boolean = false,
    val showPasswordsMismatch: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val passwordsMatch: Boolean get() = password == confirmPassword
}

internal sealed interface RegisterUiEffect {
    data class NavigateToLogin(val email: String?): RegisterUiEffect
    data class ShowSnackBar(val message: String): RegisterUiEffect
}