package com.flexaura.createpost

import android.net.Uri

sealed interface CreatePostUiIntent {
    data class OnContentChange(val newContent: String) : CreatePostUiIntent
    data class OnImageSelected(val imageUri: Uri?) : CreatePostUiIntent
    data object OnRemoveImageClicked : CreatePostUiIntent
    data object OnSubmitClicked : CreatePostUiIntent
}

data class CreatePostUiState(
    val content: String = "",
    val imageUri: Uri? = null,
    val isPostPublishing: Boolean = false,
    val errorMessage: String? = null
)

sealed interface CreatePostUiEffect {
    data class ShowSnackBar(val message: String) : CreatePostUiEffect
    data object NavigateBack : CreatePostUiEffect
}