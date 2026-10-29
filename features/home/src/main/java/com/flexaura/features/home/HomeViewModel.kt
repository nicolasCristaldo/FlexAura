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
import com.flexaura.features.home.HomeUiEffect.NavigateToAuthorProfile
import com.flexaura.features.home.HomeUiEffect.NavigateToPostDetail
import com.flexaura.features.home.HomeUiEffect.ShowSnackBar
import com.flexaura.features.home.HomeUiIntent.LoadDiscoverPosts
import com.flexaura.features.home.HomeUiIntent.LoadFollowingPosts
import com.flexaura.features.home.HomeUiIntent.OnAuthorClicked
import com.flexaura.features.home.HomeUiIntent.OnDeletePostClicked
import com.flexaura.features.home.HomeUiIntent.OnPostClicked
import com.flexaura.features.home.HomeUiIntent.OnSavedClicked
import com.flexaura.features.home.HomeUiIntent.OnVoteClicked
import com.flexaura.features.home.HomeUiIntent.Refresh
import com.flexaura.features.home.HomeUiIntent.SyncPosts
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

/**
 * ViewModel responsible for managing home screen feeds, pagination state for discover and following timelines,
 * user authentication observation, post voting, bookmarking, and deletion workflows.
 *
 * @property getAuthUserUseCase Use case for observing the current authentication state.
 * @property getPostsUseCase Use case for observing local discover posts.
 * @property getFollowingPostsUseCase Use case for observing local following feed posts.
 * @property syncPostsUseCase Use case for synchronizing discover posts from remote.
 * @property syncFollowingPostsUseCase Use case for synchronizing following posts from remote.
 * @property votePostUseCase Use case for casting or updating votes on posts.
 * @property toggleSavePostUseCase Use case for toggling post bookmark/save states.
 * @property deletePostUseCase Use case for deleting posts.
 */
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getFollowingPostsUseCase: GetFollowingPostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
    private val syncFollowingPostsUseCase: SyncFollowingPostsUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {
    private val pageSize = 20

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState(isLoading = true)
    )
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<HomeUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<HomeUiEffect> get() = _uiEffect

    init {
        observeLocalDataBase()
        observeAuthUser()
        handleIntent(SyncPosts)
    }

    /**
     * Processes incoming [HomeUiIntent] actions from the UI and updates state or triggers workflows.
     *
     * @param intent The [HomeUiIntent] to handle.
     */
    fun handleIntent(intent: HomeUiIntent) = when (intent) {
        is SyncPosts -> {
            loadDiscoverPosts()
            loadFollowingPosts()
        }

        is Refresh -> refresh()
        is LoadDiscoverPosts -> loadDiscoverPosts(intent.isRetry)
        is LoadFollowingPosts -> loadFollowingPosts(intent.isRetry)
        is OnSavedClicked -> toggleSavePost(postId = intent.postId)
        is OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )

        is OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is OnDeletePostClicked -> deletePost(intent.postId)
    }

    /**
     * Observes the local database for discover and following posts and updates the UI state accordingly.
     */
    private fun observeLocalDataBase() = viewModelScope.launch {
        combine(
            getPostsUseCase(),
            getFollowingPostsUseCase()
        ) { discover, following ->
            discover to following
        }.collect { (discover, following) ->
            _uiState.update {
                it.copy(
                    discover = it.discover.copy(items = discover),
                    following = it.following.copy(items = following)
                )
            }
        }
    }

    /**
     * Observes the authenticated user and updates the UI state accordingly.
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
     * Loads discover posts from the remote data source and updates the UI state accordingly.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadDiscoverPosts(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.discover.endReached ||
            state.discover.isLoadingMore ||
            (state.discover.hasPaginationError && !isRetry)
        ) return
        val isInitialLoad = state.discover.currentPage == 1 && state.discover.isEmpty

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedDiscoverPagination = state.discover.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(discover = updatedDiscoverPagination)
            }

            val result = syncPostsUseCase(page = state.discover.currentPage, pageSize = pageSize)

            result.fold(
                onSuccess = { fetchedCount ->
                    _uiState.update { state ->
                        val isEndReached = fetchedCount < pageSize
                        val updatedDiscoverPagination = state.discover.copy(
                            isLoadingMore = false,
                            endReached = isEndReached,
                            currentPage = state.discover.currentPage + 1
                        )
                        state.copy(
                            discover = updatedDiscoverPagination,
                            isLoading = if (isInitialLoad) false else state.isLoading
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        if(isInitialLoad) {
                            state.copy(
                                errorMessage = error.toUserReadableMessage(),
                                isLoading = false
                            )
                        }
                        else {
                            state.copy(
                                discover = state.discover.copy(
                                    isLoadingMore = false,
                                    hasPaginationError = true
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    /**
     * Loads following posts from the remote data source and updates the UI state accordingly.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadFollowingPosts(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.following.endReached ||
            state.following.isLoadingMore ||
            (state.following.hasPaginationError && !isRetry)
        ) return
        val isInitialLoad = state.following.currentPage == 1 && state.following.isEmpty

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedFollowingPagination = state.following.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(following = updatedFollowingPagination)
            }

            val result = syncFollowingPostsUseCase(page = state.following.currentPage, pageSize = pageSize)

            result.fold(
                onSuccess = { fetchedCount ->
                    _uiState.update { state ->
                        val isEndReached = fetchedCount < pageSize
                        val updatedFollowingPagination = state.following.copy(
                            isLoadingMore = false,
                            endReached = isEndReached,
                            currentPage = state.following.currentPage + 1
                        )
                        state.copy(
                            following = updatedFollowingPagination,
                            isLoading = if (isInitialLoad) false else state.isLoading
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        if(isInitialLoad) {
                            state.copy(
                                errorMessage = error.toUserReadableMessage(),
                                isLoading = false
                            )
                        }
                        else {
                            state.copy(
                                following = state.following.copy(
                                    isLoadingMore = false,
                                    hasPaginationError = true
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    /**
     * Refreshes the feeds (pull-to-refresh) and updates the UI state accordingly.
     */
    private fun refresh() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isRefreshing = true,
                following = it.following.copy(currentPage = 1, endReached = false),
                discover = it.discover.copy(currentPage = 1, endReached = false),
                isLoading = false,
                errorMessage = null
            )
        }

        val discoverRes = async { syncPostsUseCase(page = 1, pageSize = pageSize) }
        val followingRes = async { syncFollowingPostsUseCase(page = 1, pageSize = pageSize) }

        discoverRes.await()
        followingRes.await()

        _uiState.update {
            it.copy(
                isRefreshing = false,
                following = it.following.copy(currentPage = 2),
                discover = it.discover.copy(currentPage = 2)
            )
        }
    }

    /**
     * Votes on a post with the provided parameters and updates the UI state accordingly.
     *
     * @param postId The ID of the post to vote on.
     * @param vote The target vote value.
     * @param currentVote The user's current vote value prior to this action.
     */
    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error voting post."))
            }
    }

    /**
     * Toggles the bookmark/save state of a post with the provided ID and updates the UI state accordingly.
     *
     * @param postId The ID of the post to toggle the bookmark/save state for.
     */
    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar("Error saving post."))
            }
    }

    /**
     * Deletes a post with the provided ID and updates the UI state accordingly.
     *
     * @param postId The ID of the post to delete.
     */
    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar("Error deleting post."))
            }
    }

    /**
     * Emits a one-off [HomeUiEffect] to the UI.
     *
     * @param effect The [HomeUiEffect] to emit.
     */
    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}