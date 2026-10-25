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

/**
 * ViewModel responsible for managing search query operations across user profiles and posts,
 * handling paginated search result updates, optimistic vote and bookmark toggling, and post deletions.
 *
 * @property getAuthUserUseCase Use case for observing the current authentication state.
 * @property searchPostsUseCase Use case for querying posts matching a keyword.
 * @property searchUsersUseCase Use case for querying user profiles matching a keyword.
 * @property votePostUseCase Use case for casting or updating votes on posts.
 * @property deletePostUseCase Use case for deleting posts.
 * @property toggleSavePostUseCase Use case for toggling post bookmark/save states.
 */
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase
) : ViewModel() {
    private val pageSize = 10

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchUiEffect>()
    val uiEffect: SharedFlow<SearchUiEffect> get() = _uiEffect.asSharedFlow()

    init {
        observeAuthUser()
    }

    /**
     * Processes incoming [SearchUiIntent] actions from the UI and updates state or triggers workflows.
     *
     * @param intent The [SearchUiIntent] to handle.
     */
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
        is SearchUiIntent.LoadPosts -> loadPosts(intent.isRetry)
        is SearchUiIntent.LoadUsers -> loadUsers(intent.isRetry)
    }

    /**
     * Observes the current authentication state and updates the UI state accordingly.
     */
    private fun observeAuthUser() = viewModelScope.launch {
        getAuthUserUseCase().collect { authState ->
            when (authState) {
                is AuthState.Authenticated -> {
                    _uiState.update { it.copy(currentUserId = authState.userId) }
                }
                is AuthState.Unauthenticated -> {
                    _uiState.update { it.copy(currentUserId = null) }
                }
                is AuthState.Loading -> Unit
            }
        }
    }

    /**
     * Initiates a search request with the provided query.
     *
     * @param query The search keyword.
     */
    private fun search(query: String) = viewModelScope.launch {
        if (query.isBlank()) return@launch
        _uiState.update { it.copy(isSearching = true, errorMessage = null, hasSearched = true) }

        _uiState.update {
            it.copy(
                isSearching = true,
                errorMessage = null,
                hasSearched = true,
                postsResults = it.postsResults.copy(items = emptyList(), currentPage = 1, endReached = false),
                usersResults = it.usersResults.copy(items = emptyList(), currentPage = 1, endReached = false)
            )
        }
        try {
            val usersDeferred = async { searchUsersUseCase(query, 1, pageSize) }
            val postsDeferred = async { searchPostsUseCase(query, 1, pageSize) }

            val usersResult = usersDeferred.await()
            val postsResult = postsDeferred.await()

            if (usersResult.isFailure || postsResult.isFailure) {
                val error = usersResult.exceptionOrNull() ?: postsResult.exceptionOrNull()
                handleSearchError(error)
                return@launch
            }

            val users = usersResult.getOrDefault(emptyList())
            val posts = postsResult.getOrDefault(emptyList())

            _uiState.update {
                it.copy(
                    postsResults = it.postsResults.copy(
                        items = postsResult.getOrDefault(emptyList()),
                        currentPage = 2,
                        endReached = posts.size < pageSize
                    ),
                    usersResults = it.usersResults.copy(
                        items = usersResult.getOrDefault(emptyList()),
                        currentPage = 2,
                        endReached = users.size < pageSize
                    ),
                    isSearching = false,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            handleSearchError(e)
        }
    }

    /**
     * Loads user profiles with pagination support.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadUsers(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.usersResults.endReached ||
            state.usersResults.isLoadingMore ||
            (state.usersResults.hasPaginationError && !isRetry)
        ) return

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedUsersPagination = state.usersResults.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(usersResults = updatedUsersPagination)
            }

            val result = searchUsersUseCase(
                query = state.query,
                page = state.usersResults.currentPage,
                pageSize = pageSize
            )

            result.fold(
                onSuccess = { users ->
                    val isEndReached = users.size < pageSize
                    val updatedUsersPagination = state.usersResults.copy(
                        isLoadingMore = false,
                        endReached = isEndReached,
                        currentPage = state.usersResults.currentPage + 1,
                        items = state.usersResults.items + users
                    )
                    _uiState.update { it.copy(usersResults = updatedUsersPagination) }
                },
                onFailure = {
                    _uiState.update { state ->
                        val updatedUsersPagination = state.usersResults.copy(
                            isLoadingMore = false,
                            hasPaginationError = true
                        )
                        state.copy(usersResults = updatedUsersPagination)
                    }
                }
            )
        }
    }

    /**
     * Loads posts with pagination support.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadPosts(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.postsResults.endReached ||
            state.postsResults.isLoadingMore ||
            (state.postsResults.hasPaginationError && !isRetry)
        ) return

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedPostsPagination = state.postsResults.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(postsResults = updatedPostsPagination)
            }

            val result = searchPostsUseCase(
                query = state.query,
                page = state.postsResults.currentPage,
                pageSize = pageSize
            )

            result.fold(
                onSuccess = { posts ->
                    val isEndReached = posts.size < pageSize
                    val updatedPostsPagination = state.postsResults.copy(
                        isLoadingMore = false,
                        endReached = isEndReached,
                        currentPage = state.postsResults.currentPage + 1,
                        items = state.postsResults.items + posts
                    )
                    _uiState.update { it.copy(postsResults = updatedPostsPagination) }
                },
                onFailure = {
                    _uiState.update { state ->
                        val updatedPostsPagination = state.postsResults.copy(
                            isLoadingMore = false,
                            hasPaginationError = true
                        )
                        state.copy(postsResults = updatedPostsPagination)
                    }
                }
            )
        }
    }

    /**
     * Toggles the save/bookmark state of a post.
     *
     * @param postId The unique identifier of the post.
     */
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
                _uiEffect.emit(ShowSnackBar("Error saving post."))
            }
    }

    /**
     * Handles casting or updating a post's vote.
     *
     * @param postId The unique identifier of the post being voted on.
     */
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
                emitEffect(ShowSnackBar("Error voting post."))
            }
    }

    /**
     * Updates the state of a post in the UI.
     *
     * @param postId The unique identifier of the post to update.
     */
    private fun updatePostInState(postId: String, update: (Post) -> Post) {
        _uiState.update { state ->
            val updatedPosts = state.postsResults.items.map { post ->
                if (post.id == postId) update(post) else post
            }

            state.copy(postsResults = state.postsResults.copy(items = updatedPosts))
        }
    }

    /**
     * Deletes a post.
     *
     * @param postId The unique identifier of the post to delete.
     */
    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar("Error deleting post."))
            }
    }

    /**
     * Emits a one-off side effect to the UI.
     *
     * @param effect The [SearchUiEffect] to emit.
     */
    private fun emitEffect(effect: SearchUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }

    /**
     * Handles errors during a search request.
     *
     * @param e The [Throwable] representing the error.
     */
    private fun handleSearchError(e: Throwable?) {
        _uiState.update {
            it.copy(
                isSearching = false,
                errorMessage = e?.toUserReadableMessage()
            )
        }
    }
}