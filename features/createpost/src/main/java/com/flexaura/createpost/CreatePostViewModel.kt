package com.flexaura.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.post.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CreatePostUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onIntent(intent: CreatePostUiIntent) = when(intent) {
        is CreatePostUiIntent.OnImageSelected -> _uiState.update {
            it.copy(imageUri = intent.imageUri)
        }
        is CreatePostUiIntent.OnContentChange -> _uiState.update {
            it.copy(content = intent.newContent)
        }
        CreatePostUiIntent.OnSubmitClicked -> createPost(
            content = uiState.value.content,
            imageUrl = uiState.value.imageUri
        )
    }

    private fun createPost(
        content: String = "",
        imageUrl: String? = null
    ) = viewModelScope.launch {
        if (content.isBlank() && imageUrl.isNullOrBlank()) {
            emitEffect(CreatePostUiEffect.ShowSnackBar("The post cannot be empty"))
            return@launch
        }

        _uiState.update { it.copy(isPostPublishing = true) }

        createPostUseCase(content = content, imageUrl = imageUrl)
            .onSuccess {
                _uiState.update { it.copy(isPostPublishing = false) }
                emitEffect(CreatePostUiEffect.NavigateBack)
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isPostPublishing = false,
                        errorMessage = error.localizedMessage
                    )
                }
                emitEffect(
                    CreatePostUiEffect.ShowSnackBar(
                        error.localizedMessage ?: "Unknown error"
                    )
                )
            }
    }

    private fun emitEffect(effect: CreatePostUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}