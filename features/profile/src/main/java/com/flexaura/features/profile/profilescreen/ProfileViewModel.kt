package com.flexaura.features.profile.profilescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
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
    private val getAuthUserUseCase: GetAuthUserUseCase
) : ViewModel() {
    private val argumentUserId: String? = savedStateHandle.toRoute<ProfileDestinations.ProfileScreen>().userId

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(
        ProfileUiState(
            isLoading = true
        )
    )
    val uiState: StateFlow<ProfileUiState> get() = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<ProfileUiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<ProfileUiEffect> get() = _uiEffect

    init {
        handleIntent(ProfileUiIntent.Init)
    }

    fun handleIntent(intent: ProfileUiIntent) = when (intent) {
        is ProfileUiIntent.Init -> {
            observeProfileData()
            refreshData()
        }
        is ProfileUiIntent.Refresh -> {
            observeProfileData()
            refreshData()
        }
        is ProfileUiIntent.OnToggleFollowClicked -> { toggleFollowUser() }
        is ProfileUiIntent.OnVoteClicked -> {
            handleVote(intent.postId, intent.vote, intent.currentVote)
        }
        is ProfileUiIntent.OnSavedClicked -> { toggleSave(intent.postId) }
        is ProfileUiIntent.OnPostClicked -> emitEffect(ProfileUiEffect.NavigateToPostDetail(intent.postId))
        is ProfileUiIntent.OnDeletePostClicked -> deletePost(intent.postId)
        is ProfileUiIntent.OnAuthorClicked -> emitEffect(
            ProfileUiEffect.NavigateToAuthorProfile(
                intent.authorId
            )
        )
    }

    private fun observeProfileData() = viewModelScope.launch {
        getAuthUserUseCase().flatMapLatest { authState ->
            val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
            val targetUserId = argumentUserId ?: currentLoggedUserId

            if (targetUserId == null) {
                flowOf(ProfileUiState(isLoading = true))
            } else {
                val isOwnProfile = targetUserId == currentLoggedUserId

                combine(
                    getUserByIdUseCase(targetUserId),
                    getPostsByAuthorUseCase(targetUserId),
                    if (isOwnProfile) getSavedPostsUseCase() else flowOf(emptyList()),
                ) { user, posts, savedPosts ->
                    ProfileUiState(
                        currentUserId = currentLoggedUserId,
                        user = user,
                        posts = posts,
                        savedPosts = savedPosts,
                        isOwnProfile = isOwnProfile,
                        isLoading = false
                    )
                }
            }
        }.collect { updatedState ->
            _uiState.value = updatedState
        }
    }

    private fun refreshData() = viewModelScope.launch {
        val authState = getAuthUserUseCase().first()
        val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
        val targetUserId = argumentUserId ?: currentLoggedUserId ?: return@launch
        val isOwnProfile = targetUserId == currentLoggedUserId

        _uiState.update { it.copy(isRefreshing = true) }

        val userSync = launch {
            syncUserUseCase(targetUserId).onFailure { error ->
                emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
            }
        }
        val syncPosts = launch {
            syncPostsByAuthorUseCase(targetUserId).onFailure { error ->
                emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
            }
        }
        val syncSavedPosts = if(isOwnProfile) {
            launch {
                syncSavedPostsUseCase().onFailure { error ->
                    emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
                }
            }
        } else null

        userSync.join()
        syncPosts.join()
        syncSavedPosts?.join()

        _uiState.update { it.copy(isRefreshing = false) }
    }

    private fun toggleFollowUser() = viewModelScope.launch {
        val authState = getAuthUserUseCase().first()
        val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
        val targetUserId = argumentUserId ?: currentLoggedUserId ?: return@launch

        if (targetUserId != currentLoggedUserId) {
            toggleFollowUserUseCase(targetUserId).onFailure { error ->
                emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
            }
        }
    }

    private fun handleVote(postId: String, value: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = value, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
            }
    }

    private fun toggleSave(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId).onFailure { error ->
            emitEffect(ProfileUiEffect.ShowSnackBar(error.message ?: "Unknown error"))
        }
    }

    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(
                    ProfileUiEffect.ShowSnackBar(
                        error.localizedMessage ?: "Unknown error"
                    )
                )
            }
    }

    fun emitEffect(effect: ProfileUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}