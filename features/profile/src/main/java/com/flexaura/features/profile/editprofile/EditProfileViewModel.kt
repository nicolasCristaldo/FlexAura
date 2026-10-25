package com.flexaura.features.profile.editprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.user.GetUserByIdUseCase
import com.flexaura.data.domain.usecase.user.UpdateProfileUseCase
import com.flexaura.data.domain.util.compressAndGetImageBytes
import com.flexaura.features.profile.editprofile.EditProfileUiEffect.*
import com.flexaura.features.profile.editprofile.EditProfileUiIntent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user profile editing state, loading initial user profile data,
 * handling avatar compression, and executing profile update use cases.
 *
 * @property getAuthUserUseCase Use case for observing the current authentication state.
 * @property getUserByIdUseCase Use case for fetching user profile details by ID.
 * @property updateProfileUseCase Use case for updating profile information.
 * @property context Application context used for resolving and compressing image URIs.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class EditProfileViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<EditProfileUiEffect>()
    val uiEffect: SharedFlow<EditProfileUiEffect> = _uiEffect.asSharedFlow()

    init { onIntent(Init) }

    /**
     * Processes incoming [EditProfileUiIntent] actions from the UI and updates state or triggers workflows.
     *
     * @param intent The [EditProfileUiIntent] to handle.
     */
    fun onIntent(intent: EditProfileUiIntent) = when(intent) {
        Init -> loadInitialUserData()
        is OnUsernameChanged -> _uiState.update { it.copy(username = intent.username) }
        is OnBioChanged -> _uiState.update { it.copy(bio = intent.bio) }
        is OnDisplayNameChanged -> _uiState.update { it.copy(displayName = intent.displayName) }
        is OnImageChanged -> _uiState.update { it.copy(selectedImageUri = intent.imageUri, isImageRemoved = false) }
        is OnRemoveImage -> _uiState.update {
            it.copy(isImageRemoved = true, selectedImageUri = null, currentImageUrl = null)
        }
        is OnSaveProfile -> saveProfile()
    }

    /**
     * Observes the authenticated user and initiates data fetching.
     */
    private fun loadInitialUserData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        getAuthUserUseCase().collect { authState ->
            when (authState) {
                is AuthState.Authenticated -> {
                    fetchUserData(authState.userId)
                }
                is AuthState.Unauthenticated -> {
                    _uiState.update { it.copy(isLoading = false) }
                    emitEffect(NavigateBack)
                }
                is AuthState.Loading -> Unit
            }
        }
    }

    /**
     * Fetches user profile details by ID and updates the UI state accordingly.
     */
    private fun fetchUserData(userId: String) = viewModelScope.launch {
        try {
            getUserByIdUseCase(userId).collect { user ->
                if(user != null) {
                    _uiState.update {
                        it.copy(
                            username = user.username,
                            displayName = user.displayName,
                            bio = user.bio ?: "",
                            currentImageUrl = user.imageUrl,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                else {
                    _uiState.update { it.copy(isLoading = false, error = "User not found.") }
                }
            }
        } catch(e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Error fetching user data.") }
        }
    }

    /**
     * Initiates the profile update process with the provided content and optional image bytes.
     */
    private fun saveProfile() = viewModelScope.launch {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }

        val imageBytes = state.selectedImageUri?.let { uri ->
            context.compressAndGetImageBytes(uri = uri, quality = 50)
        }

        updateProfileUseCase(
            username = state.username,
            displayName = state.displayName,
            imageBytes = imageBytes,
            bio = state.bio,
            isImageRemoved = state.isImageRemoved
        ).fold(
            onSuccess = {
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(NavigateBack)
            },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(ShowSnackBar("Error saving profile."))
            }
        )
    }

    /**
     * Emits a one-off [EditProfileUiEffect] to the UI.
     *
     * @param effect The [EditProfileUiEffect] to emit.
     */
    private fun emitEffect(effect: EditProfileUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}