package com.flexaura.features.auth.login

/**
 * Sealed interface representing user actions and intents triggered from the login UI.
 */
internal sealed interface LoginUiIntent {
    /**
     * Intent triggered when the user modifies the email input field.
     *
     * @property email The updated email string.
     */
    data class OnEmailChanged(val email: String): LoginUiIntent
    /**
     * Intent triggered when the user modifies the password input field.
     *
     * @property password The updated password string.
     */
    data class OnPasswordChanged(val password: String): LoginUiIntent
    /** Intent triggered when the user toggles password visibility. */
    data object OnShowPasswordChanged: LoginUiIntent
    /** Intent triggered when the user clicks the login action button. */
    data object OnLoginClicked: LoginUiIntent
    /** Intent triggered when the user initiates Google sign-in. */
    data object OnGoogleLoginClicked: LoginUiIntent
    /** Intent triggered when the user requests navigation to the registration screen. */
    data object OnRegisterNavigated: LoginUiIntent
    /** Intent triggered when the user dismisses the email verification dialog. */
    data object OnDismissVerificationDialog: LoginUiIntent
}

/**
 * Data class representing the UI state of the login screen.
 *
 * @property email The current email input value.
 * @property password The current password input value.
 * @property isLoading Flag indicating whether an authentication request is in progress.
 * @property showPassword Flag indicating whether password characters are visible.
 * @property showVerificationDialog Flag indicating whether the email verification dialog is displayed.
 * @property showErrors Flag indicating whether input validation errors should be rendered.
 * @property errorMessage Error message describing a failed authentication attempt, or `null`.
 */
internal data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val showPassword: Boolean = false,
    val showVerificationDialog: Boolean = false,
    val showErrors: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Sealed interface representing one-off side effects emitted by the [LoginViewModel] to the UI.
 */
internal sealed interface LoginUiEffect {
    /** Effect requesting navigation to the registration screen. */
    data object NavigateToRegister: LoginUiEffect
    /** Effect requesting navigation to the home screen upon successful authentication. */
    data object NavigateToHome: LoginUiEffect
    /** Effect requesting the launch of the Google Sign-In prompt or flow. */
    data object LaunchGoogleSignIn: LoginUiEffect
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String): LoginUiEffect
}