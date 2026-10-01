package com.flexaura.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.core.designsystem.util.toUserReadableMessage
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.post.DeletePostUseCase
import com.flexaura.data.domain.usecase.post.SearchPostsUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.data.domain.usecase.user.SearchUsersUseCase
import com.flexaura.search.SearchUiEffect.NavigateToAuthorProfile
import com.flexaura.search.SearchUiEffect.NavigateToPostDetail
import com.flexaura.search.SearchUiEffect.ShowSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchUiEffect>()
    val uiEffect: SharedFlow<SearchUiEffect> get() = _uiEffect.asSharedFlow()

    init {
        observeAuthUser()
    }

    fun onIntent(intent: SearchUiIntent) = when (intent) {
        is SearchUiIntent.OnSearchClicked -> {
            if (!_uiState.value.hasSearched) _uiState.update { it.copy(hasSearched = true) }
            search(intent.searchText)
        }
        is SearchUiIntent.OnTextChange -> _uiState.update { it.copy(query = intent.newText) }
        is SearchUiIntent.Refresh -> search(uiState.value.query)
        is SearchUiIntent.OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is SearchUiIntent.OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is SearchUiIntent.OnSavedClicked -> toggleSavePost(intent.postId)
        is SearchUiIntent.OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )

        is SearchUiIntent.OnDeletePostClicked -> deletePost(intent.postId)
    }

    private fun observeAuthUser() = viewModelScope.launch {
        getAuthUserUseCase().collect { authState ->
            if (authState is AuthState.Authenticated) {
                _uiState.update { it.copy(currentUserId = authState.userId) }
            }
        }
    }

    private fun search(query: String) = viewModelScope.launch {
        if (query.isBlank()) return@launch
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }

        try {
            val usersDeferred = async { searchUsersUseCase(query, 1, 10) }
            val postsDeferred = async { searchPostsUseCase(query, 1, 10) }

            val usersResult = usersDeferred.await()
            val postsResult = postsDeferred.await()

            if (usersResult.isFailure || postsResult.isFailure) {
                val error = (usersResult.exceptionOrNull() ?: postsResult.exceptionOrNull())
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = error?.toUserReadableMessage()
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    postsResults = postsResult.getOrDefault(emptyList()),
                    usersResults = usersResult.getOrDefault(emptyList()),
                    isSearching = false,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    errorMessage = e.toUserReadableMessage()
                )
            }
        }
    }

    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        updatePostInState(postId = postId) { post ->
            val newSaveState = !post.isSavedByUser
            val newSavesCount = if (newSaveState) post.savesCount + 1
            else (post.savesCount - 1).coerceAtLeast(0)

            post.copy(isSavedByUser = newSaveState, savesCount = newSavesCount)
        }

        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                updatePostInState(postId = postId) { post ->
                    val revertSaveState = !post.isSavedByUser
                    val revertSavesCount = if (revertSaveState) post.savesCount + 1
                    else (post.savesCount - 1).coerceAtLeast(0)

                    post.copy(isSavedByUser = revertSaveState, savesCount = revertSavesCount)
                }
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        updatePostInState(postId = postId) { post ->
            val diff = vote - currentVote
            val newTotalAura = post.totalAura + diff
            post.copy(currentUserVote = vote, totalAura = newTotalAura)
        }

        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                updatePostInState(postId = postId) { post ->
                    val revertTotalAura = post.totalAura - (vote - currentVote)

                    post.copy(currentUserVote = currentVote, totalAura = revertTotalAura)
                }
                emitEffect(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun updatePostInState(postId: String, update: (Post) -> Post) {
        _uiState.update { currentState ->
            val updatedPosts = currentState.postsResults.map { post ->
                if (post.id == postId) update(post) else post
            }

            currentState.copy(postsResults = updatedPosts)
        }
    }

    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun emitEffect(effect: SearchUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}