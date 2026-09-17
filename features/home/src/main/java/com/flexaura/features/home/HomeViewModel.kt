package com.flexaura.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.core.designsystem.util.toUserReadableMessage
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.post.DeletePostUseCase
import com.flexaura.data.domain.usecase.post.GetFollowingPostsUseCase
import com.flexaura.data.domain.usecase.post.GetPostsUseCase
import com.flexaura.data.domain.usecase.post.SyncFollowingPostsUseCase
import com.flexaura.data.domain.usecase.post.SyncPostsUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.features.home.HomeUiEffect.*
import com.flexaura.features.home.HomeUiIntent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getFollowingPostsUseCase: GetFollowingPostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
    private val syncFollowingPostsUseCase: SyncFollowingPostsUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<HomeUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<HomeUiEffect> get() = _uiEffect

    init {
        observeLocalDataBase()
        observeAuthUser()
        handleIntent(Init)
    }

    fun handleIntent(intent: HomeUiIntent) = when (intent) {
        is Init -> syncPosts()
        is OnSavedClicked -> toggleSavePost(postId = intent.postId)
        is OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )
        is Refresh -> syncPosts()
        is OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is OnDeletePostClicked -> deletePost(intent.postId)
    }

    private fun observeLocalDataBase() = viewModelScope.launch {
        combine(
            getPostsUseCase(),
            getFollowingPostsUseCase()
        ) { discover, following ->
            discover to following
        }.collect { (discover, following) ->
            _uiState.update {
                it.copy(
                    discoverPosts = discover,
                    followingPosts = following
                )
            }
        }
    }

    private fun observeAuthUser() = viewModelScope.launch {
        getAuthUserUseCase().collect { authState ->
            if (authState is AuthState.Authenticated) {
                _uiState.update { it.copy(currentUserId = authState.userId) }
            }
        }
    }

    private fun syncPosts(isRefresh: Boolean = false) = viewModelScope.launch {
        val pageToFetch = if (isRefresh) 1 else uiState.value.currentPage

        _uiState.update {
            if (isRefresh) it.copy(isRefreshing = true)
            else it.copy(isLoading = true)
        }

        try {
            val discoverSync = async { syncPostsUseCase(page = pageToFetch) }
            val followingSync = async { syncFollowingPostsUseCase(page = pageToFetch) }

            val discoverResult = discoverSync.await()
            val followingResult = followingSync.await()

            if (discoverResult.isFailure || followingResult.isFailure) {
                val error = discoverResult.exceptionOrNull() ?: followingResult.exceptionOrNull()

                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        errorMessage = error?.toUserReadableMessage()
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    currentPage = if (isRefresh) 1 else it.currentPage + 1,
                    isRefreshing = false,
                    isLoading = false,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    isLoading = false,
                    errorMessage = e.toUserReadableMessage()
                )
            }
        }
    }

    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}