package com.flexaura.features.profile.editprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.user.GetUserByIdUseCase
import com.flexaura.data.domain.usecase.user.UpdateProfileUseCase
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun onIntent(intent: EditProfileUiIntent) = when(intent) {
        Init -> loadInitialUserData()
        is OnUsernameChanged -> _uiState.update { it.copy(username = intent.username) }
        is OnBioChanged -> _uiState.update { it.copy(bio = intent.bio) }
        is OnDisplayNameChanged -> _uiState.update { it.copy(displayName = intent.displayName) }
        is OnImageChanged -> _uiState.update { it.copy(selectedImageUri = intent.imageUri) }
        OnSaveProfile -> saveProfile()
    }

    private fun loadInitialUserData() = viewModelScope.launch {
        getAuthUserUseCase().flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                getUserByIdUseCase(authState.userId)
            }
            else { flowOf(null) }
        }.collect { user ->
            user?.let { currentUser ->
                _uiState.update { state ->
                    state.copy(
                        username = currentUser.username,
                        displayName = currentUser.displayName,
                        currentImageUrl = currentUser.imageUrl,
                        bio = currentUser.bio ?: "",
                    )
                }
            }
        }
    }

    private fun saveProfile() = viewModelScope.launch {
        val state = _uiState.value

        _uiState.update { it.copy(isLoading = true) }

        val imageBytes = state.selectedImageUri?.let { uri ->
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }

        updateProfileUseCase(
            username = state.username,
            displayName = state.displayName,
            imageBytes = imageBytes,
            bio = state.bio
        ).fold(
            onSuccess = {
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(NavigateBack)
            },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(ShowSnackBar(error.message ?: "Unknown error"))
            }
        )
    }

    private fun emitEffect(effect: EditProfileUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}