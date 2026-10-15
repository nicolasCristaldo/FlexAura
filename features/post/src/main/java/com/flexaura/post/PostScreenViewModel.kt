package com.flexaura.post

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.core.designsystem.util.toUserReadableMessage
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.comment.CreateCommentUseCase
import com.flexaura.data.domain.usecase.comment.DeleteCommentUseCase
import com.flexaura.data.domain.usecase.comment.GetCommentsUseCase
import com.flexaura.data.domain.usecase.comment.SyncCommentsUseCase
import com.flexaura.data.domain.usecase.post.DeletePostUseCase
import com.flexaura.data.domain.usecase.post.GetPostByIdUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.post.PostDetailUiEffect.NavigateBack
import com.flexaura.post.PostDetailUiEffect.NavigateToAuthorProfile
import com.flexaura.post.PostDetailUiEffect.ShowSnackBar
import com.flexaura.post.PostDetailUiIntent.Init
import com.flexaura.post.PostDetailUiIntent.OnCommentChanged
import com.flexaura.post.PostDetailUiIntent.OnDeleteComment
import com.flexaura.post.PostDetailUiIntent.OnDeletePost
import com.flexaura.post.PostDetailUiIntent.OnNavigateBack
import com.flexaura.post.PostDetailUiIntent.OnNavigateToAuthorProfile
import com.flexaura.post.PostDetailUiIntent.OnSavedClicked
import com.flexaura.post.PostDetailUiIntent.OnSubmitComment
import com.flexaura.post.PostDetailUiIntent.OnVoteClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PostScreenViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val syncCommentsUseCase: SyncCommentsUseCase,
    private val submitCommentUseCase: CreateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deletePostUseCase: DeletePostUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PostDetailUiEffect>()
    val uiEffect get() = _uiEffect.asSharedFlow()

    private var currentPostId: String? = null

    init { observeAuthUser() }

    fun onIntent(intent: PostDetailUiIntent) = when(intent) {
        is Init -> {
            currentPostId = intent.postId
            loadPost(intent.postId)
            observeComments(intent.postId)
            syncComments(intent.postId)
        }
        is OnSavedClicked -> toggleSavePost(intent.postId)
        is OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )
        is OnCommentChanged -> _uiState.update { it.copy(commentContent = intent.content) }
        is OnDeleteComment -> deleteComment(intent.commentId)
        is OnDeletePost -> deletePost(intent.postId)
        is OnSubmitComment -> submitComment(postId = intent.postId, content = intent.content)
        is OnNavigateBack -> emitEffect(NavigateBack)
        is OnNavigateToAuthorProfile -> emitEffect(NavigateToAuthorProfile(intent.authorId))
    }

    private fun observeAuthUser() = viewModelScope.launch {
        getAuthUserUseCase().collect { authState ->
            when(authState) {
                is AuthState.Authenticated -> {
                    _uiState.update { it.copy(currentUserId = authState.userId) }
                }
                is AuthState.Unauthenticated -> {
                    _uiState.update { it.copy(currentUserId = null) }
                    emitEffect(NavigateBack)
                }
                is AuthState.Loading -> Unit
            }
        }
    }

    private fun loadPost(postId: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            val postResult = getPostByIdUseCase(postId)

            _uiState.update { it.copy(isLoading = false, post = postResult) }
        }
        catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.toUserReadableMessage()) }
        }
    }

    private fun observeComments(postId: String) = viewModelScope.launch {
        getCommentsUseCase(postId).collect { comments ->
            _uiState.update { it.copy(comments = comments) }
        }
    }

    private fun syncComments(postId: String) = viewModelScope.launch {
        val result = syncCommentsUseCase(postId)

        if (result.isFailure) {
            emitEffect(ShowSnackBar("Error syncing comments."))
        }
    }

    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error voting post."))
            }
    }

    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error saving post."))
            }
    }

    private fun submitComment(postId: String, content: String) = viewModelScope.launch {
        submitCommentUseCase(postId = postId, content = content)
            .onSuccess {
                _uiState.update { it.copy(commentContent = "") }
            }
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error submitting comment."))
            }
    }

    private fun deleteComment(commentId: String) = viewModelScope.launch {
        deleteCommentUseCase(commentId = commentId)
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error deleting comment."))
            }
    }

    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onSuccess {
                emitEffect(NavigateBack)
            }
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error deleting post."))
            }
    }

    private fun emitEffect(effect: PostDetailUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}