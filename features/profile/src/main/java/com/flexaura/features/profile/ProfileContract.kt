package com.flexaura.features.profile

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User

sealed interface ProfileUiIntent {
    data object Init : ProfileUiIntent
    data object Refresh : ProfileUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : ProfileUiIntent
    data class OnSavedClicked(val postId: String) : ProfileUiIntent
    data object OnToggleFollowClicked : ProfileUiIntent
}

data class ProfileUiState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val currentPage: Int = 1,
    val isOwnProfile: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ProfileUiEffect {
    data class ShowSnackBar(val message: String) : ProfileUiEffect
}