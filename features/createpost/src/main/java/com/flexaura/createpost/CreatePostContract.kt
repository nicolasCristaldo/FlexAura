package com.flexaura.createpost

import android.net.Uri
import com.flexaura.data.domain.model.User

internal sealed interface CreatePostUiIntent {
    data object Init : CreatePostUiIntent
    data class OnContentChange(val newContent: String) : CreatePostUiIntent
    data class OnImageSelected(val imageUri: Uri?) : CreatePostUiIntent
    data object OnRemoveImageClicked : CreatePostUiIntent
    data object OnSubmitClicked : CreatePostUiIntent
}

internal data class CreatePostUiState(
    val currentUser: User? = null,
    val content: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isPostPublishing: Boolean = false,
    val errorMessage: String? = null
)

internal sealed interface CreatePostUiEffect {
    data class ShowSnackBar(val message: String) : CreatePostUiEffect
    data object NavigateBack : CreatePostUiEffect
}