package com.flexaura.features.profile.profilescreen

import com.flexaura.components.general.PaginationState
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User
import com.flexaura.features.profile.R
import com.flexaura.features.profile.profilescreen.components.ProfileDialogType

internal sealed interface ProfileUiIntent {
    data object LoadProfile : ProfileUiIntent
    data class LoadUserPosts(val isRetry: Boolean = false) : ProfileUiIntent
    data class LoadSavedPosts(val isRetry: Boolean = false) : ProfileUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : ProfileUiIntent
    data class OnSavedClicked(val postId: String) : ProfileUiIntent
    data object OnToggleFollowClicked : ProfileUiIntent
    data class OnPostClicked(val postId: String) : ProfileUiIntent
    data class OnDeletePostClicked(val postId: String) : ProfileUiIntent
    data class OnAuthorClicked(val authorId: String) : ProfileUiIntent
    data class ShowDialog(val dialogType: ProfileDialogType?) : ProfileUiIntent
    data object LogoutConfirmed : ProfileUiIntent
    data object DeleteAccountConfirmed : ProfileUiIntent
}

internal data class ProfileUiState(
    val currentUserId: String? = null,
    val user: User? = null,
    val posts: PaginationState<Post> = PaginationState(),
    val saved: PaginationState<Post> = PaginationState(),
    val isOwnProfile: Boolean = false,
    val dialogActive: ProfileDialogType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

internal sealed interface ProfileUiEffect {
    data class ShowSnackBar(val message: String) : ProfileUiEffect
    data class NavigateToPostDetail(val postId: String) : ProfileUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : ProfileUiEffect
    data object NavigateToAuth : ProfileUiEffect
}

internal enum class ProfileTab(val titleRes: Int) {
    POSTS(R.string.posts),
    SAVED(R.string.saved)
}