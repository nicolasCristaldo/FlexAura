package com.flexaura.features.auth.register

/**
 * Sealed interface representing user actions and intents triggered from the registration UI.
 */
internal sealed interface RegisterUiIntent {
    /**
     * Intent triggered when the user modifies the email input field.
     *
     * @property email The updated email string.
     */
    data class OnEmailChanged(val email: String): RegisterUiIntent

    /**
     * Intent triggered when the user modifies the password input field.
     *
     * @property password The updated password string.
     */
    data class OnPasswordChanged(val password: String): RegisterUiIntent

    /**
     * Intent triggered when the user modifies the confirm password input field.
     *
     * @property confirmPassword The updated confirmation password string.
     */
    data class OnConfirmPasswordChanged(val confirmPassword: String): RegisterUiIntent

    /**
     * Intent triggered when the user modifies the username input field.
     *
     * @property username The updated username string.
     */
    data class OnUsernameChanged(val username: String): RegisterUiIntent

    /**
     * Intent triggered when the user modifies the display name input field.
     *
     * @property displayName The updated display name string.
     */
    data class OnDisplayNameChanged(val displayName: String): RegisterUiIntent

    /** Intent triggered when the user clicks the register action button. */
    data object OnRegisterClicked: RegisterUiIntent

    /** Intent triggered when the user requests navigation back to the login screen. */
    data object OnLoginNavigated: RegisterUiIntent
}

/**
 * Data class representing the UI state of the registration screen.
 *
 * @property email The current email input value.
 * @property password The current password input value.
 * @property confirmPassword The current confirmation password input value.
 * @property username The current username input value.
 * @property displayName The current display name input value.
 * @property showErrors Flag indicating whether input validation errors should be rendered.
 * @property showPasswordsMismatch Flag indicating whether password mismatch errors should be displayed.
 * @property isLoading Flag indicating whether a registration request is in progress.
 * @property errorMessage Error message describing a failed registration attempt, or `null`.
 * @property passwordsMatch Computed property indicating whether the password and confirmation password fields match.
 */
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

/**
 * Sealed interface representing one-off side effects emitted by the [RegisterViewModel] to the UI.
 */
internal sealed interface RegisterUiEffect {
    /**
     * Effect requesting navigation back to the login screen.
     *
     * @property email Optional email address to pre-fill on the login screen, or `null`.
     */
    data class NavigateToLogin(val email: String?): RegisterUiEffect

    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String): RegisterUiEffect
}