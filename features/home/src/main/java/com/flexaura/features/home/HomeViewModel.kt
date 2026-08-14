package com.flexaura.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.post.GetPostsUseCase
import com.flexaura.data.domain.usecase.post.SyncPostsUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.features.home.HomeUiEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<HomeUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<HomeUiEffect> get() = _uiEffect

    init {
        observeLocalDataBase()
        handleIntent(HomeUiIntent.Init)
    }

    fun handleIntent(intent: HomeUiIntent) = when (intent) {
        is HomeUiIntent.Init -> syncPosts()
        is HomeUiIntent.OnSavedClicked -> toggleSavePost(postId = intent.postId)
        is HomeUiIntent.OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )
        is HomeUiIntent.Refresh -> syncPosts()
        is HomeUiIntent.OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is HomeUiIntent.OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
    }

    private fun observeLocalDataBase() = viewModelScope.launch {
        getPostsUseCase().collect { posts ->
            _uiState.update {
                it.copy(
                    discoverPosts = posts,
                    followingPosts = posts.filter { it.author?.isFollowedByCurrentUser == true }
                )
            }
        }
    }

    private fun syncPosts(isRefresh: Boolean = false) = viewModelScope.launch {
        val pageToFetch = if (isRefresh) 1 else uiState.value.currentPage

        _uiState.update {
            if (isRefresh) it.copy(isRefreshing = true)
            else it.copy(isLoading = true)
        }

        syncPostsUseCase(page = pageToFetch)
            .onSuccess {
                _uiState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        currentPage = if (isRefresh) 2 else oldState.currentPage + 1
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = error.localizedMessage) }
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
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

    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}