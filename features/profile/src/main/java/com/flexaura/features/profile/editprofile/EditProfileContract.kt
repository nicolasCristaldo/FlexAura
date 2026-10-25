package com.flexaura.features.profile.editprofile

import android.net.Uri
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername

/**
 * Sealed interface representing user actions and intents triggered from the edit profile screen.
 */
internal sealed interface EditProfileUiIntent {
    /** Intent triggered to initialize the screen and load initial user profile data. */
    data object Init : EditProfileUiIntent

    /**
     * Intent triggered when the user modifies the username input field.
     *
     * @property username The updated username string.
     */
    data class OnUsernameChanged(val username: String) : EditProfileUiIntent

    /**
     * Intent triggered when the user modifies the display name input field.
     *
     * @property displayName The updated display name string.
     */
    data class OnDisplayNameChanged(val displayName: String) : EditProfileUiIntent

    /**
     * Intent triggered when the user modifies the biography text field.
     *
     * @property bio The updated biography string.
     */
    data class OnBioChanged(val bio: String) : EditProfileUiIntent

    /**
     * Intent triggered when a new avatar image is selected.
     *
     * @property imageUri The selected image [Uri], or `null`.
     */
    data class OnImageChanged(val imageUri: Uri?) : EditProfileUiIntent

    /** Intent triggered when the user removes their current avatar image. */
    data object OnRemoveImage : EditProfileUiIntent

    /** Intent triggered when the user clicks the save profile action button. */
    data object OnSaveProfile : EditProfileUiIntent
}

/**
 * Data class representing the UI state of the edit profile screen.
 *
 * @property username The current username input value.
 * @property displayName The current display name input value.
 * @property bio The current biography input value.
 * @property currentImageUrl The public URL of the user's existing avatar image, or `null`.
 * @property selectedImageUri The [Uri] of a newly selected local avatar image, or `null`.
 * @property isImageRemoved Flag indicating whether the existing avatar image is marked for removal.
 * @property isLoading Flag indicating whether profile data loading or saving is in progress.
 * @property error Error message describing a failure, or `null`.
 */
internal data class EditProfileUiState(
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val currentImageUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val isImageRemoved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Validates whether the save button should be enabled based on input validation rules.
     *
     * @return `true` if inputs are valid and not loading, `false` otherwise.
     */
    fun saveButtonEnabled(): Boolean =
        !isLoading && username.isValidUsername() && displayName.isValidLength(1, 25) && bio.isValidLength(0, 200)
}

/**
 * Sealed interface representing one-off side effects emitted by the [EditProfileViewModel] to the UI.
 */
internal sealed interface EditProfileUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : EditProfileUiEffect

    /** Effect requesting navigation back to the previous screen. */
    data object NavigateBack : EditProfileUiEffect
}