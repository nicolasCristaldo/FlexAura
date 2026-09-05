package com.flexaura.post

import com.flexaura.data.domain.model.Comment
import com.flexaura.data.domain.model.Post

sealed interface PostDetailUiIntent {
    data class Init(val postId: String) : PostDetailUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : PostDetailUiIntent
    data class OnSavedClicked(val postId: String) : PostDetailUiIntent
    data class OnCommentChanged(val content: String) : PostDetailUiIntent
    data class OnSubmitComment(val postId: String, val content: String) : PostDetailUiIntent
    data class OnDeleteComment(val commentId: String) : PostDetailUiIntent
    data class OnDeletePost(val postId: String) : PostDetailUiIntent
    data object OnNavigateBack : PostDetailUiIntent
    data class OnNavigateToAuthorProfile(val authorId: String) : PostDetailUiIntent
}

data class PostDetailUiState(
    val post: Post? = null,
    val currentUserId: String? = null,
    val commentContent: String = "",
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface PostDetailUiEffect {
    data class ShowSnackBar(val message: String) : PostDetailUiEffect
    data object NavigateBack : PostDetailUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : PostDetailUiEffect
}