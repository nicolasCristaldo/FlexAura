package com.flexaura.post

import com.flexaura.data.domain.model.Post

sealed interface PostDetailUiIntent {
    data class Init(val postId: String) : PostDetailUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : PostDetailUiIntent
    data class OnSavedClicked(val postId: String) : PostDetailUiIntent
}

data class PostDetailUiState(
    val post: Post? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface PostDetailUiEffect {
    data class ShowSnackBar(val message: String) : PostDetailUiEffect
}