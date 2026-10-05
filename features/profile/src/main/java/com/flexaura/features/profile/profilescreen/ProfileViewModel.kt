package com.flexaura.features.profile.profilescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.flexaura.data.domain.repository.AuthState
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
    private val logoutUseCase: LogoutUseCase
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
        is ProfileUiIntent.OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is ProfileUiIntent.OnDeletePostClicked -> deletePost(intent.postId)
        is ProfileUiIntent.OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is ProfileUiIntent.ShowLogoutDialog -> _uiState.update { it.copy(showDialog = intent.show) }
        is ProfileUiIntent.LogoutConfirmed -> {
            _uiState.update { it.copy(isLoading = true, showDialog = false) }
            logout()
        }
    }

    private fun observeProfileData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        getAuthUserUseCase().flatMapLatest { authState ->
            val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
            val targetUserId = argumentUserId ?: currentLoggedUserId

            if (targetUserId == null) {
                flowOf(null)
            } else {
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
            val (user, posts, savedPosts) = combinedData

            _uiState.update {
                it.copy(
                    user = user,
                    posts = posts,
                    savedPosts = savedPosts,
                    isOwnProfile = isOwnProfile,
                    isLoading = false,
                    errorMessage = if (user == null) "User not found" else null
                )
            }
        }
    }

    private fun refreshData() = viewModelScope.launch {
        val authState = getAuthUserUseCase().first()
        val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
        val targetUserId = argumentUserId ?: currentLoggedUserId ?: return@launch
        val isOwnProfile = targetUserId == currentLoggedUserId
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        try {
            val userSync = async { syncUserUseCase(targetUserId) }
            val postsSync = async { syncPostsByAuthorUseCase(targetUserId) }
            val savedSync = if (isOwnProfile) async { syncSavedPostsUseCase() } else null

            val userRes = userSync.await()
            val postsRes = postsSync.await()
            val savedPostsRes = savedSync?.await()

            val error = userRes.exceptionOrNull() ?: postsRes.exceptionOrNull()
            ?: savedPostsRes?.exceptionOrNull()
            if (error != null) {
                handleSyncError(error)
            }
        } catch(e: Exception) {
            handleSyncError(e)
        } finally {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun toggleFollowUser() = viewModelScope.launch {
        val authState = getAuthUserUseCase().first()
        val currentLoggedUserId = (authState as? AuthState.Authenticated)?.userId
        val targetUserId = argumentUserId ?: currentLoggedUserId ?: return@launch

        if (targetUserId != currentLoggedUserId) {
            toggleFollowUserUseCase(targetUserId).onFailure { error ->
                emitEffect(ShowSnackBar(error.message ?: "Unknown error"))
            }
        }
    }

    private fun handleVote(postId: String, value: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = value, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar(error.message ?: "Unknown error"))
            }
    }

    private fun toggleSave(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId).onFailure { error ->
            emitEffect(ShowSnackBar(error.message ?: "Unknown error"))
        }
    }

    private fun deletePost(postId: String) = viewModelScope.launch {
        deletePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(
                    ShowSnackBar(
                        error.localizedMessage ?: "Unknown error"
                    )
                )
            }
    }

    private fun logout() = viewModelScope.launch {
        logoutUseCase().fold(
            onSuccess = { emitEffect(NavigateToAuth) },
            onFailure = {
                _uiState.update { state -> state.copy(isLoading = false) }
                emitEffect(ShowSnackBar(it.message ?: "Unknown error"))
            }
        )
    }

    private fun emitEffect(effect: ProfileUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }

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