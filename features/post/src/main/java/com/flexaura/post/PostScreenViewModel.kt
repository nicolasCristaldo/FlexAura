package com.flexaura.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.post.GetPostByIdUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostScreenViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PostDetailUiEffect>()
    val uiEffect get() = _uiEffect.asSharedFlow()

    private var currentPostId: String? = null

    fun onIntent(intent: PostDetailUiIntent) = when(intent) {
        is PostDetailUiIntent.Init -> {
            currentPostId = intent.postId
            loadPost(intent.postId)
        }
        is PostDetailUiIntent.OnSavedClicked -> toggleSavePost(intent.postId)
        is PostDetailUiIntent.OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )
    }

    private fun loadPost(postId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val postDeferred = async { getPostByIdUseCase(postId) }
        val postResult = postDeferred.await()

        _uiState.update { it.copy(isLoading = false, post = postResult) }
    }

    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                _uiEffect.emit(PostDetailUiEffect.ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(PostDetailUiEffect.ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }
}