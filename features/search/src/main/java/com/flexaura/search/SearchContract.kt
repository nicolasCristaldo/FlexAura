package com.flexaura.search

import com.flexaura.components.general.PaginationState
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User

/**
 * Sealed interface representing user actions and intents triggered from the search screen.
 */
internal sealed interface SearchUiIntent {
    /** Intent triggered to refresh search results. */
    data object Refresh : SearchUiIntent

    /**
     * Intent triggered to load more matching user profiles with pagination support.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadUsers(val isRetry: Boolean = false) : SearchUiIntent

    /**
     * Intent triggered to load more matching posts with pagination support.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadPosts(val isRetry: Boolean = false) : SearchUiIntent

    /**
     * Intent triggered when the user modifies the search text input.
     *
     * @property newText The updated search query string.
     */
    data class OnTextChange(val newText: String) : SearchUiIntent

    /**
     * Intent triggered when the user explicitly initiates a search request.
     *
     * @property searchText The search query keyword.
     */
    data class OnSearchClicked(val searchText: String) : SearchUiIntent

    /**
     * Intent triggered when the user clicks on a post from search results.
     *
     * @property postId The unique identifier of the selected post.
     */
    data class OnPostClicked(val postId: String) : SearchUiIntent

    /**
     * Intent triggered when the user casts or updates a vote on a post in search results.
     *
     * @property postId The unique identifier of the post being voted on.
     * @property vote The target vote value.
     * @property currentVote The user's current vote value prior to this action.
     */
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : SearchUiIntent

    /**
     * Intent triggered when the user toggles the save/bookmark state of a post in search results.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnSavedClicked(val postId: String) : SearchUiIntent

    /**
     * Intent triggered when the user clicks on an author's profile link or avatar.
     *
     * @property authorId The unique identifier of the author.
     */
    data class OnAuthorClicked(val authorId: String) : SearchUiIntent

    /**
     * Intent triggered when the user requests to delete a post.
     *
     * @property postId The unique identifier of the post to delete.
     */
    data class OnDeletePostClicked(val postId: String) : SearchUiIntent
}

/**
 * Data class representing the UI state of the search screen.
 *
 * @property currentUserId The unique identifier of the currently authenticated user, or `null`.
 * @property query The current search query string.
 * @property isSearching Flag indicating whether a search request is currently executing.
 * @property postsResults Pagination state managing matching post search results.
 * @property usersResults Pagination state managing matching user profile search results.
 * @property errorMessage Error message describing a failure, or `null`.
 * @property hasSearched Flag indicating whether at least one search query has been performed.
 */
internal data class SearchUiState(
    val currentUserId: String? = null,
    val query: String = "",
    val isSearching: Boolean = false,
    val postsResults: PaginationState<Post> = PaginationState(),
    val usersResults: PaginationState<User> = PaginationState(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

/**
 * Sealed interface representing one-off side effects emitted by the [SearchViewModel] to the UI.
 */
sealed interface SearchUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : SearchUiEffect

    /**
     * Effect requesting navigation to a post detail screen.
     *
     * @property postId The unique identifier of the post to view.
     */
    data class NavigateToPostDetail(val postId: String) : SearchUiEffect

    /**
     * Effect requesting navigation to an author's profile screen.
     *
     * @property authorId The unique identifier of the author.
     */
    data class NavigateToAuthorProfile(val authorId: String) : SearchUiEffect
}

/**
 * Enum representing the available tabs in the search screen.
 *
 * @property titleRes The string resource ID for the tab title.
 */
internal enum class SearchTab(val titleRes: Int) {
    PROFILES(R.string.profiles),
    POSTS(R.string.posts)
}