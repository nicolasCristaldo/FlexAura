package com.flexaura.createpost

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.core.designsystem.util.toUserReadableMessage
import com.flexaura.data.domain.usecase.post.CreatePostUseCase
import com.flexaura.createpost.CreatePostUiEffect.*
import com.flexaura.createpost.CreatePostUiIntent.*
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.user.GetUserByIdUseCase
import com.flexaura.data.domain.util.compressAndGetImageBytes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing post creation state, observing the authenticated user,
 * handling media compression, and executing the create post use case.
 *
 * @property createPostUseCase Use case for publishing a new post with content and optional image bytes.
 * @property getAuthUserUseCase Use case for observing the current authentication state.
 * @property getUserByIdUseCase Use case for fetching user profile details by ID.
 * @property context Application context used for resolving image content URIs and compression.
 */
@HiltViewModel
internal class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CreatePostUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        onIntent(Init)
    }

    /**
     * Processes incoming [CreatePostUiIntent] actions from the UI and updates state or triggers workflows.
     *
     * @param intent The [CreatePostUiIntent] to handle.
     */
    fun onIntent(intent: CreatePostUiIntent) = when (intent) {
        Init -> observeAuthUser()
        is OnImageSelected -> _uiState.update { it.copy(imageUri = intent.imageUri) }
        is OnContentChange -> _uiState.update { it.copy(content = intent.newContent) }
        OnSubmitClicked -> createPost()
        OnRemoveImageClicked -> _uiState.update { it.copy(imageUri = null) }
    }

    /**
     * Observes the authenticated user and initiates data fetching.
     */
    private fun observeAuthUser() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        getAuthUserUseCase().collect { authState ->
            when (authState) {
                is AuthState.Authenticated -> observeUser(authState.userId)
                is AuthState.Unauthenticated -> {
                    _uiState.update { it.copy(isLoading = false) }
                    emitEffect(NavigateBack)
                }

                is AuthState.Loading -> Unit
            }
        }
    }

    /**
     * Observes the profile details of a user with the provided ID.
     *
     * @param userId The ID of the user to observe.
     */
    private fun observeUser(userId: String) = viewModelScope.launch {
        getUserByIdUseCase(userId)
            .catch { error ->
                _uiState.update {
                    it.copy(errorMessage = error.toUserReadableMessage(), isLoading = false)
                }
            }
            .collect { user ->
                if (user != null) _uiState.update { it.copy(currentUser = user, isLoading = false) }
                else _uiState.update { it.copy(errorMessage = "User not found.", isLoading = false) }
            }
    }

    /**
     * Initiates the post creation process with the provided content and optional image bytes.
     */
    private fun createPost() = viewModelScope.launch {
        val content = uiState.value.content
        val imageUri = uiState.value.imageUri

        if (content.isBlank() && imageUri == null) {
            emitEffect(ShowSnackBar("The post cannot be empty."))
            return@launch
        }

        _uiState.update { it.copy(isPostPublishing = true) }

        val imageBytes = imageUri?.let { uri -> context.compressAndGetImageBytes(uri = uri) }

        createPostUseCase(content = content, imageBytes = imageBytes)
            .onSuccess {
                _uiState.update { it.copy(isPostPublishing = false) }
                emitEffect(NavigateBack)
            }
            .onFailure { error ->
                _uiState.update { it.copy(isPostPublishing = false) }
                emitEffect(ShowSnackBar(error.localizedMessage ?: "Error creating post."))
            }
    }

    /**
     * Emits a one-off [CreatePostUiEffect] to the UI.
     *
     * @param effect The [CreatePostUiEffect] to emit.
     */
    private fun emitEffect(effect: CreatePostUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}