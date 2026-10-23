package com.flexaura.search

import com.flexaura.components.general.PaginationState
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User

internal sealed interface SearchUiIntent {
    data object Refresh : SearchUiIntent
    data class LoadUsers(val isRetry: Boolean = false) : SearchUiIntent
    data class LoadPosts(val isRetry: Boolean = false) : SearchUiIntent
    data class OnTextChange(val newText: String) : SearchUiIntent
    data class OnSearchClicked(val searchText: String) : SearchUiIntent
    data class OnPostClicked(val postId: String) : SearchUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : SearchUiIntent
    data class OnSavedClicked(val postId: String) : SearchUiIntent
    data class OnAuthorClicked(val authorId: String) : SearchUiIntent
    data class OnDeletePostClicked(val postId: String) : SearchUiIntent
}

internal data class SearchUiState(
    val currentUserId: String? = null,
    val query: String = "",
    val isSearching: Boolean = false,
    val postsResults: PaginationState<Post> = PaginationState(),
    val usersResults: PaginationState<User> = PaginationState(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

sealed interface SearchUiEffect {
    data class ShowSnackBar(val message: String) : SearchUiEffect
    data class NavigateToPostDetail(val postId: String) : SearchUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : SearchUiEffect
}

internal enum class SearchTab(val titleRes: Int) {
    PROFILES(R.string.profiles),
    POSTS(R.string.posts)
}