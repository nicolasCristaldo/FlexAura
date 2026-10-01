package com.flexaura.features.home

import com.flexaura.data.domain.model.Post

internal sealed interface HomeUiIntent {
    data object Init : HomeUiIntent
    data object Refresh : HomeUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : HomeUiIntent
    data class OnSavedClicked(val postId: String) : HomeUiIntent
    data class OnPostClicked(val postId: String) : HomeUiIntent
    data class OnAuthorClicked(val authorId: String) : HomeUiIntent
    data class OnDeletePostClicked(val postId: String) : HomeUiIntent
}

internal data class HomeUiState(
    val currentUserId: String? = null,
    val discoverPosts: List<Post> = emptyList(),
    val followingPosts: List<Post> = emptyList(),
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

internal sealed interface HomeUiEffect {
    data class ShowSnackBar(val message: String) : HomeUiEffect
    data class NavigateToPostDetail(val postId: String) : HomeUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : HomeUiEffect
}

internal enum class HomeTab(val title: String) {
    DISCOVER("Discover"),
    FOLLOWING("Following")
}