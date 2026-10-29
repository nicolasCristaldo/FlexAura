package com.flexaura.features.profile.profilescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.flexaura.core.designsystem.util.toUserReadableMessage
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.DeleteAccountUseCase
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import com.flexaura.data.domain.usecase.auth.LogoutUseCase
import com.flexaura.data.domain.usecase.post.DeletePostUseCase
import com.flexaura.data.domain.usecase.post.GetPostsByAuthorUseCase
import com.flexaura.data.domain.usecase.post.GetSavedPostsUseCase
import com.flexaura.data.domain.usecase.post.SyncPostsByAuthorUseCase
import com.flexaura.data.domain.usecase.post.SyncSavedPostsUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.data.domain.usecase.user.GetUserByIdUseCase
import com.flexaura.data.domain.usecase.user.SyncUserUseCase
import com.flexaura.data.domain.usecase.user.ToggleFollowUserUseCase
import com.flexaura.features.profile.navigation.ProfileDestinations
import com.flexaura.features.profile.profilescreen.ProfileUiEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user profile details, pagination states for user posts and saved items,
 * follow actions, post voting, bookmarking, deletion, logout, and account deletion workflows.
 *
 * @property savedStateHandle Handle to saved state parameters used to extract target user navigation arguments.
 * @property getUserByIdUseCase Use case for observing user profile details by ID.
 * @property getPostsByAuthorUseCase Use case for observing posts authored by a user.
 * @property getSavedPostsUseCase Use case for observing saved posts (if viewing own profile).
 * @property syncUserUseCase Use case for synchronizing user profile data from remote.
 * @property syncPostsByAuthorUseCase Use case for synchronizing author posts from remote.
 * @property syncSavedPostsUseCase Use case for synchronizing saved posts from remote.
 * @property toggleFollowUserUseCase Use case for toggling follow/unfollow states on users.
 * @property toggleSavePostUseCase Use case for toggling post bookmark/save states.
 * @property votePostUseCase Use case for casting or updating votes on posts.
 * @property deletePostUseCase Use case for deleting posts.
 * @property getAuthUserUseCase Use case for observing the current authentication state.
 * @property logoutUseCase Use case for terminating the user session.
 * @property deleteAccountUseCase Use case for deleting the user account.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class
ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getPostsByAuthorUseCase: GetPostsByAuthorUseCase,
    private val getSavedPostsUseCase: GetSavedPostsUseCase,
    private val syncUserUseCase: SyncUserUseCase,
    private val syncPostsByAuthorUseCase: SyncPostsByAuthorUseCase,
    private val syncSavedPostsUseCase: SyncSavedPostsUseCase,
    private val toggleFollowUserUseCase: ToggleFollowUserUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val getAuthUserUseCase: GetAuthUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {
    val pageSize = 10
    private val argumentUserId: String? = savedStateHandle.toRoute<ProfileDestinations.ProfileScreen>().userId

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(
        ProfileUiState(isLoading = true)
    )
    val uiState: StateFlow<ProfileUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<ProfileUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<ProfileUiEffect> get() = _uiEffect

    init {
        handleIntent(ProfileUiIntent.LoadProfile)
    }

    /**
     * Processes incoming [ProfileUiIntent] actions from the UI and updates state or triggers workflows.
     *
     * @param intent The [ProfileUiIntent] to handle.
     */
    fun handleIntent(intent: ProfileUiIntent) = when (intent) {
        is ProfileUiIntent.LoadProfile -> {
            observeProfileData()
            syncProfileData()
        }
        is ProfileUiIntent.LoadSavedPosts -> loadSavedPosts(intent.isRetry)
        is ProfileUiIntent.LoadUserPosts -> loadUserPosts(intent.isRetry)
        is ProfileUiIntent.OnToggleFollowClicked -> { toggleFollowUser() }
        is ProfileUiIntent.OnVoteClicked -> {
            handleVote(intent.postId, intent.vote, intent.currentVote)
        }
        is ProfileUiIntent.OnSavedClicked -> { toggleSave(intent.postId) }
        is ProfileUiIntent.OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is ProfileUiIntent.OnDeletePostClicked -> deletePost(intent.postId)
        is ProfileUiIntent.OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is ProfileUiIntent.ShowDialog -> _uiState.update { it.copy(dialogActive = intent.dialogType) }
        is ProfileUiIntent.LogoutConfirmed -> {
            _uiState.update { it.copy(isLoading = true, dialogActive = null) }
            logout()
        }
        is ProfileUiIntent.DeleteAccountConfirmed -> {
            _uiState.update { it.copy(isLoading = true, dialogActive = null) }
            deleteAccount()
        }
    }

    /**
     * Retrieves the target user ID for profile data synchronization.
     */
    private fun getTargetUserId(): String? =
        if (argumentUserId != null) argumentUserId
        else {
            val state = _uiState.value
            state.currentUserId
        }

    /**
     * Observes the profile data and updates the UI state accordingly.
     */
    private fun observeProfileData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        getAuthUserUseCase().flatMapLatest { authState ->
            val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId

            _uiState.update { it.copy(currentUserId = currentLoggedUserId) }

            val targetUserId = getTargetUserId()

            if (targetUserId == null) { flowOf(null) }
            else {
                val isOwnProfile = targetUserId == currentLoggedUserId

                combine(
                    getUserByIdUseCase(targetUserId),
                    getPostsByAuthorUseCase(targetUserId),
                    if (isOwnProfile) getSavedPostsUseCase() else flowOf(emptyList()),
                ) { user, posts, savedPosts ->
                    Triple(user, posts, savedPosts) to isOwnProfile
                }
            }
        }.collect { data ->
            if(data == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@collect
            }
            val (combinedData, isOwnProfile) = data
            val (user, userPosts, savedPosts) = combinedData

            _uiState.update {
                it.copy(
                    user = user,
                    posts = it.posts.copy(items = userPosts),
                    saved = it.saved.copy(items = savedPosts),
                    isOwnProfile = isOwnProfile,
                    isLoading = false,
                    errorMessage = if (user == null) "User not found" else null
                )
            }
        }
    }

    /**
     * Initiates the profile data synchronization process.
     */
    private fun syncProfileData() = viewModelScope.launch {
        val targetUserId = getTargetUserId() ?: return@launch
        val currentLoggedUserId = _uiState.value.currentUserId ?: return@launch
        val isOwnProfile = targetUserId == currentLoggedUserId

        _uiState.update {
            it.copy(
                errorMessage = null,
                posts = it.posts.copy(currentPage = 1, endReached = false),
                saved = it.saved.copy(currentPage = 1, endReached = false)
            )
        }

        try {
            val userSync = async { syncUserUseCase(targetUserId) }
            val postsSync = async {
                syncPostsByAuthorUseCase(userId = targetUserId, page = 1, pageSize =  pageSize)
            }
            val savedSync = if (isOwnProfile) async {
                syncSavedPostsUseCase(page = 1, pageSize = pageSize)
            } else null

            val userRes = userSync.await()
            val postsRes = postsSync.await()
            val savedPostsRes = savedSync?.await()

            _uiState.update {
                it.copy(
                    posts = it.posts.copy(currentPage = 2),
                    saved = if(isOwnProfile) it.saved.copy(currentPage = 2) else it.saved
                )
            }

            val error = userRes.exceptionOrNull() ?:
                postsRes.exceptionOrNull() ?:
                savedPostsRes?.exceptionOrNull()

            if (error != null) { handleSyncError(error) }
        } catch(e: Exception) {
            handleSyncError(e)
        }
    }

    /**
     * Loads user posts with pagination support.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadUserPosts(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.posts.endReached ||
            state.posts.isLoadingMore ||
            (state.posts.hasPaginationError && !isRetry)
        ) return
        val isInitialLoad = state.posts.currentPage == 1 && state.posts.isEmpty

        viewModelScope.launch {
            val targetUserId = getTargetUserId() ?: return@launch

            _uiState.update { state ->
                val updatedPostsPagination = state.posts.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(posts = updatedPostsPagination)
            }

            val result = syncPostsByAuthorUseCase(
                userId = targetUserId,
                page = state.posts.currentPage,
                pageSize = pageSize
            )

            result.fold(
                onSuccess = { fetchedCount ->
                    _uiState.update { state ->
                        val isEndReached = fetchedCount < pageSize
                        val updatedPostsPagination = state.posts.copy(
                            isLoadingMore = false,
                            endReached = isEndReached,
                            currentPage = state.posts.currentPage + 1
                        )
                        state.copy(posts = updatedPostsPagination)
                    }
                },
                onFailure = { error ->
                    if (isInitialLoad) _uiState.update { state ->
                        state.copy(errorMessage = error.toUserReadableMessage(), isLoading = false)
                    }
                    else _uiState.update { state ->
                        val updatedPostsPagination = state.posts.copy(
                            isLoadingMore = false,
                            hasPaginationError = true
                        )
                        state.copy(posts = updatedPostsPagination)
                    }
                }
            )
        }
    }

    /**
     * Loads saved posts with pagination support.
     *
     * @param isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    private fun loadSavedPosts(isRetry: Boolean = false) {
        val state = _uiState.value
        if (
            state.saved.endReached ||
            state.saved.isLoadingMore ||
            (state.saved.hasPaginationError && !isRetry)
        ) return
        val isInitialLoad = state.saved.currentPage == 1 && state.saved.isEmpty

        viewModelScope.launch {
            _uiState.update { state ->
                val updatedSavedPagination = state.saved.copy(
                    isLoadingMore = true,
                    hasPaginationError = false
                )
                state.copy(saved = updatedSavedPagination)
            }

            val result = syncSavedPostsUseCase(
                page = state.saved.currentPage,
                pageSize = pageSize
            )

            result.fold(
                onSuccess = { fetchedCount ->
                    _uiState.update { state ->
                        val isEndReached = fetchedCount < pageSize
                        val updatedSavedPagination = state.saved.copy(
                            isLoadingMore = false,
                            endReached = isEndReached,
                            currentPage = state.saved.currentPage + 1
                        )
                        state.copy(saved = updatedSavedPagination)
                    }
                },
                onFailure = { error ->
                    if (isInitialLoad) _uiState.update { state ->
                        state.copy(errorMessage = error.toUserReadableMessage(), isLoading = false)
                    }
                    else _uiState.update { state ->
                        val updatedSavedPagination = state.saved.copy(
                            isLoadingMore = false,
                            hasPaginationError = true
                        )
                        state.copy(saved = updatedSavedPagination)
                    }
                }
            )
        }
    }

    /**
     * Toggles the follow/unfollow state of a user.
     */
    private fun toggleFollowUser() = viewModelScope.launch {
        val state = _uiState.value
        val currentLoggedUserId = state.currentUserId
        val targetUserId = argumentUserId ?: currentLoggedUserId ?: return@launch

        if (targetUserId != currentLoggedUserId) {
            toggleFollowUserUseCase(targetUserId).onFailure { error ->
                emitEffect(ShowSnackBar("Error following user."))
            }
        }
    }

    /**
     * Handles casting or updating a post's vote.
     *
     * @param postId The unique identifier of the post being voted on.
     * @param value The target vote value.
     * @param currentVote The user's current vote value prior to this action.
     */
    private fun handleVote(postId: String, value: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = value, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar("Error voting post."))
            }
    }

    /**
     * Toggles the save/bookmark state of a post.
     *
     * @param postId The unique identifier of the post.
     */
    private fun toggleSave(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId).onFailure { error ->
            emitEffect(ShowSnackBar("Error saving post."))
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
     * Initiates the user session logout process.
     */
    private fun logout() = viewModelScope.launch {
        logoutUseCase().fold(
            onSuccess = { emitEffect(NavigateToAuth) },
            onFailure = {
                _uiState.update { state -> state.copy(isLoading = false) }
                emitEffect(ShowSnackBar("Error logging out."))
            }
        )
    }

    /**
     * Initiates the account deletion process.
     */
    private fun deleteAccount() = viewModelScope.launch {
        deleteAccountUseCase().fold(
            onSuccess = { emitEffect(NavigateToAuth) },
            onFailure = {
                _uiState.update { state -> state.copy(isLoading = false) }
                emitEffect(ShowSnackBar("Error deleting account."))
            }
        )
    }

    /**
     * Emits a one-off [ProfileUiEffect] to the UI.
     *
     * @param effect The [ProfileUiEffect] to emit.
     */
    private fun emitEffect(effect: ProfileUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }

    /**
     * Handles any errors during profile data synchronization.
     *
     * @param e The [Throwable] error object.
     */
    private fun handleSyncError(e: Throwable) {
        val errorMessage = e.localizedMessage ?: "Unknown error"
        val hasData = _uiState.value.user != null

        if(hasData) {
            emitEffect(ShowSnackBar(errorMessage))
        }
        else {
            _uiState.update { it.copy(errorMessage = errorMessage) }
        }
    }
}