package com.flexaura.features.profile.profilescreen

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User

internal sealed interface ProfileUiIntent {
    data object Init : ProfileUiIntent
    data object Refresh : ProfileUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : ProfileUiIntent
    data class OnSavedClicked(val postId: String) : ProfileUiIntent
    data object OnToggleFollowClicked : ProfileUiIntent
    data class OnPostClicked(val postId: String) : ProfileUiIntent
    data class OnDeletePostClicked(val postId: String) : ProfileUiIntent
    data class OnAuthorClicked(val authorId: String) : ProfileUiIntent
    data class ShowLogoutDialog(val show: Boolean = true) : ProfileUiIntent
    data object LogoutConfirmed : ProfileUiIntent
}

internal data class ProfileUiState(
    val currentUserId: String? = null,
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val savedPosts: List<Post> = emptyList(),
    val currentPage: Int = 1,
    val isOwnProfile: Boolean = false,
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

internal sealed interface ProfileUiEffect {
    data class ShowSnackBar(val message: String) : ProfileUiEffect
    data class NavigateToPostDetail(val postId: String) : ProfileUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : ProfileUiEffect
    data object NavigateToAuth : ProfileUiEffect
}

internal enum class ProfileTab(val title: String) {
    POSTS("Posts"),
    SAVED("Saved")
}