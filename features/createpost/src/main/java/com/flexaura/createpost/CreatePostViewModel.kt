package com.flexaura.createpost

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init { onIntent(Init) }

    fun onIntent(intent: CreatePostUiIntent) = when(intent) {
        Init -> observeAuthUser()
        is OnImageSelected -> _uiState.update {
            it.copy(imageUri = intent.imageUri)
        }
        is OnContentChange -> _uiState.update {
            it.copy(content = intent.newContent)
        }
        OnSubmitClicked -> createPost()
        OnRemoveImageClicked -> _uiState.update { it.copy(imageUri = null) }
    }

    private fun observeAuthUser() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

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

    private fun observeUser(userId: String) = viewModelScope.launch {
        getUserByIdUseCase(userId)
            .catch { error ->
                handleError(error.localizedMessage ?: "Unknown error")
            }
            .collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(currentUser = user, isLoading = false) }
                } else {
                    handleError("User not found")
                }
            }
    }

    private fun createPost() = viewModelScope.launch {
        val content = uiState.value.content
        val imageUri = uiState.value.imageUri

        if (content.isBlank() && imageUri == null) {
            emitEffect(ShowSnackBar("The post cannot be empty"))
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
                _uiState.update {
                    it.copy(
                        isPostPublishing = false,
                        errorMessage = error.localizedMessage
                    )
                }
                emitEffect(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun emitEffect(effect: CreatePostUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }

    private fun handleError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
        emitEffect(ShowSnackBar(message))
    }
}