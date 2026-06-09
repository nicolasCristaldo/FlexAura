package com.flexaura.features.home

import com.flexaura.data.domain.model.Post

sealed interface HomeUiIntent {
    data object Init : HomeUiIntent
    data object Refresh : HomeUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : HomeUiIntent
    data class OnSavedClicked(val postId: String) : HomeUiIntent
    data class OnNewPostTextChanged(val newText: String) : HomeUiIntent
    data object OnPublishPostClicked : HomeUiIntent
}

data class HomeUiState(
    val posts: List<Post> = emptyList(),
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val newPostText: String = "",
    val isPostPublishing: Boolean = false
)

sealed interface HomeUiEffect {
    data class ShowSnackBar(val message: String) : HomeUiEffect
}