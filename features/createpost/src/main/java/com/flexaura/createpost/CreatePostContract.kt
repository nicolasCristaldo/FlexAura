package com.flexaura.createpost

import android.net.Uri
import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.util.isValidLength

/**
 * Sealed interface representing user actions and intents triggered from the create post UI.
 */
internal sealed interface CreatePostUiIntent {
    /** Intent triggered to initialize the screen and observe authenticated user data. */
    data object Init : CreatePostUiIntent

    /**
     * Intent triggered when the user modifies the post text content.
     *
     * @property newContent The updated content string.
     */
    data class OnContentChange(val newContent: String) : CreatePostUiIntent

    /**
     * Intent triggered when an image is selected from the gallery.
     *
     * @property imageUri The selected image [Uri], or `null` if deselected.
     */
    data class OnImageSelected(val imageUri: Uri?) : CreatePostUiIntent

    /** Intent triggered when the user clicks the action to remove the attached image. */
    data object OnRemoveImageClicked : CreatePostUiIntent

    /** Intent triggered when the user clicks the submit/publish post button. */
    data object OnSubmitClicked : CreatePostUiIntent
}

/**
 * Data class representing the UI state of the create post screen.
 *
 * @property currentUser The profile [User] currently authenticated and creating the post, or `null`.
 * @property content The text content of the post.
 * @property imageUri The [Uri] of the attached image, or `null`.
 * @property isLoading Flag indicating whether the initial user data is loading.
 * @property isPostPublishing Flag indicating whether a post publication request is currently in progress.
 * @property errorMessage Error message describing a failure, or `null`.
 * @property isSubmitButtonEnabled Computed property indicating whether the submit button is active.
 */
internal data class CreatePostUiState(
    val currentUser: User? = null,
    val content: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isPostPublishing: Boolean = false,
    val errorMessage: String? = null
) {
    val isSubmitButtonEnabled = (imageUri != null || content.isValidLength(1, 280)) && !isPostPublishing
}

/**
 * Sealed interface representing one-off side effects emitted by the [CreatePostViewModel] to the UI.
 */
internal sealed interface CreatePostUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : CreatePostUiEffect

    /** Effect requesting navigation back to the previous screen. */
    data object NavigateBack : CreatePostUiEffect
}