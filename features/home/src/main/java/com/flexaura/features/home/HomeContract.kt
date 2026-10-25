package com.flexaura.features.home

import com.flexaura.components.general.PaginationState
import com.flexaura.data.domain.model.Post

/**
 * Sealed interface representing user actions and intents triggered from the home screen feeds.
 */
internal sealed interface HomeUiIntent {
    /** Intent triggered to synchronize initial posts across feeds. */
    data object SyncPosts : HomeUiIntent

    /** Intent triggered to refresh the feeds (pull-to-refresh). */
    data object Refresh : HomeUiIntent

    /**
     * Intent triggered to load more discover posts.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadDiscoverPosts(val isRetry: Boolean = false) : HomeUiIntent

    /**
     * Intent triggered to load more following feed posts.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadFollowingPosts(val isRetry: Boolean = false) : HomeUiIntent

    /**
     * Intent triggered when the user casts or updates a vote on a post.
     *
     * @property postId The unique identifier of the post being voted on.
     * @property vote The target vote value.
     * @property currentVote The user's current vote value prior to this action.
     */
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : HomeUiIntent

    /**
     * Intent triggered when the user clicks the save/bookmark action on a post.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnSavedClicked(val postId: String) : HomeUiIntent

    /**
     * Intent triggered when the user clicks on a post to view its details.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnPostClicked(val postId: String) : HomeUiIntent

    /**
     * Intent triggered when the user clicks on an author's profile link or avatar.
     *
     * @property authorId The unique identifier of the author.
     */
    data class OnAuthorClicked(val authorId: String) : HomeUiIntent

    /**
     * Intent triggered when the user requests to delete a post.
     *
     * @property postId The unique identifier of the post to delete.
     */
    data class OnDeletePostClicked(val postId: String) : HomeUiIntent
}

/**
 * Data class representing the UI state of the home screen.
 *
 * @property currentUserId The unique identifier of the currently authenticated user, or `null`.
 * @property discover Pagination state managing the discover/global post feed.
 * @property following Pagination state managing the following timeline feed.
 * @property isLoading Flag indicating whether initial data loading is in progress.
 * @property isRefreshing Flag indicating whether a pull-to-refresh operation is active.
 * @property errorMessage Error message describing a failure, or `null`.
 */
internal data class HomeUiState(
    val currentUserId: String? = null,
    val discover: PaginationState<Post> = PaginationState(),
    val following: PaginationState<Post> = PaginationState(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Sealed interface representing one-off side effects emitted by the [HomeViewModel] to the UI.
 */
internal sealed interface HomeUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : HomeUiEffect

    /**
     * Effect requesting navigation to a post detail screen.
     *
     * @property postId The unique identifier of the post to view.
     */
    data class NavigateToPostDetail(val postId: String) : HomeUiEffect

    /**
     * Effect requesting navigation to an author's profile screen.
     *
     * @property authorId The unique identifier of the author.
     */
    data class NavigateToAuthorProfile(val authorId: String) : HomeUiEffect
}

/**
 * Enum representing the available tabs in the home feed.
 *
 * @property titleRes The string resource ID for the tab title.
 */
internal enum class HomeTab(val titleRes: Int) {
    DISCOVER(R.string.discover),
    FOLLOWING(R.string.following)
}