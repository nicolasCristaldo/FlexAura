package com.flexaura.search

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User

sealed interface SearchUiIntent {
    data object Refresh : SearchUiIntent
    data class OnTextChange(val newText: String) : SearchUiIntent
    data class OnSearchClicked(val searchText: String) : SearchUiIntent
    data class OnTabChanged(val tab: SearchTab) : SearchUiIntent
    data class OnPostClicked(val postId: String) : SearchUiIntent
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : SearchUiIntent
    data class OnSavedClicked(val postId: String) : SearchUiIntent
    data class OnAuthorClicked(val authorId: String) : SearchUiIntent
}

data class SearchUiState(
    val query: String = "",
    val selectedTab: SearchTab = SearchTab.PROFILES,
    val isSearching: Boolean = false,
    val postsResults: List<Post> = emptyList(),
    val usersResults: List<User> = emptyList(),
    val errorMessage: String? = null
)

sealed interface SearchUiEffect {
    data class ShowSnackBar(val message: String) : SearchUiEffect
    data class NavigateToPostDetail(val postId: String) : SearchUiEffect
    data class NavigateToAuthorProfile(val authorId: String) : SearchUiEffect
}

enum class SearchTab(val title: String) {
    PROFILES("Profiles"),
    POSTS("Posts")
}