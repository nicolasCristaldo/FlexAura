package com.flexaura.features.profile.editprofile

import android.net.Uri
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername

internal sealed interface EditProfileUiIntent {
    data object Init : EditProfileUiIntent
    data class OnUsernameChanged(val username: String) : EditProfileUiIntent
    data class OnDisplayNameChanged(val displayName: String) : EditProfileUiIntent
    data class OnBioChanged(val bio: String) : EditProfileUiIntent
    data class OnImageChanged(val imageUri: Uri?) : EditProfileUiIntent
    data object OnSaveProfile : EditProfileUiIntent
}

internal data class EditProfileUiState(
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val currentImageUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun saveButtonEnabled(): Boolean =
        !isLoading && username.isValidUsername() && displayName.isValidLength(1, 25) && bio.isValidLength(0, 150)
}

internal sealed interface EditProfileUiEffect {
    data class ShowSnackBar(val message: String) : EditProfileUiEffect
    data object NavigateBack : EditProfileUiEffect
}