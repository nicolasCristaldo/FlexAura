package com.flexaura.features.profile.profilescreen

import com.flexaura.components.general.PaginationState
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User
import com.flexaura.features.profile.R
import com.flexaura.features.profile.profilescreen.components.ProfileDialogType

/**
 * Sealed interface representing user actions and intents triggered from the profile screen.
 */
internal sealed interface ProfileUiIntent {
    /** Intent triggered to load profile details and synchronize initial data. */
    data object LoadProfile : ProfileUiIntent

    /**
     * Intent triggered to load more user-authored posts with pagination support.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadUserPosts(val isRetry: Boolean = false) : ProfileUiIntent

    /**
     * Intent triggered to load more saved/bookmarked posts with pagination support.
     *
     * @property isRetry Flag indicating whether this is a retry attempt after a pagination error.
     */
    data class LoadSavedPosts(val isRetry: Boolean = false) : ProfileUiIntent

    /**
     * Intent triggered when the user casts or updates a vote on a post.
     *
     * @property postId The unique identifier of the post being voted on.
     * @property vote The target vote value.
     * @property currentVote The user's current vote value prior to this action.
     */
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : ProfileUiIntent

    /**
     * Intent triggered when the user toggles the save/bookmark state of a post.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnSavedClicked(val postId: String) : ProfileUiIntent

    /** Intent triggered when the user clicks the follow or unfollow action on another user's profile. */
    data object OnToggleFollowClicked : ProfileUiIntent

    /**
     * Intent triggered when the user clicks on a post to view its details.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnPostClicked(val postId: String) : ProfileUiIntent

    /**
     * Intent triggered when the user requests to delete a post.
     *
     * @property postId The unique identifier of the post to delete.
     */
    data class OnDeletePostClicked(val postId: String) : ProfileUiIntent

    /**
     * Intent triggered when the user clicks on an author's profile link or avatar.
     *
     * @property authorId The unique identifier of the author.
     */
    data class OnAuthorClicked(val authorId: String) : ProfileUiIntent

    /**
     * Intent triggered to show or dismiss a confirmation dialog.
     *
     * @property dialogType The [ProfileDialogType] to display, or `null` to dismiss.
     */
    data class ShowDialog(val dialogType: ProfileDialogType?) : ProfileUiIntent

    /** Intent triggered when the user confirms session logout. */
    data object LogoutConfirmed : ProfileUiIntent

    /** Intent triggered when the user confirms account deletion. */
    data object DeleteAccountConfirmed : ProfileUiIntent
}

/**
 * Data class representing the UI state of the profile screen.
 *
 * @property currentUserId The unique identifier of the currently authenticated user, or `null`.
 * @property user The profile [User] details, or `null` if loading.
 * @property posts Pagination state managing the user's authored posts.
 * @property saved Pagination state managing the user's saved/bookmarked posts.
 * @property isOwnProfile Flag indicating whether the viewed profile belongs to the authenticated user.
 * @property dialogActive The currently active [ProfileDialogType] confirmation dialog, or `null`.
 * @property isLoading Flag indicating whether initial profile data is loading.
 * @property errorMessage Optional error message describing a failure, or `null`.
 */
internal data class ProfileUiState(
    val currentUserId: String? = null,
    val user: User? = null,
    val posts: PaginationState<Post> = PaginationState(),
    val saved: PaginationState<Post> = PaginationState(),
    val isOwnProfile: Boolean = false,
    val dialogActive: ProfileDialogType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Sealed interface representing one-off side effects emitted by the [ProfileViewModel] to the UI.
 */
internal sealed interface ProfileUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : ProfileUiEffect

    /**
     * Effect requesting navigation to a post detail screen.
     *
     * @property postId The unique identifier of the post to view.
     */
    data class NavigateToPostDetail(val postId: String) : ProfileUiEffect

    /**
     * Effect requesting navigation to an author's profile screen.
     *
     * @property authorId The unique identifier of the author.
     */
    data class NavigateToAuthorProfile(val authorId: String) : ProfileUiEffect

    /** Effect requesting navigation back to the authentication/login screen upon logout or account deletion. */
    data object NavigateToAuth : ProfileUiEffect
}

/**
 * Enum representing the available tabs in the profile view.
 *
 * @property titleRes The string resource ID for the tab title.
 */
internal enum class ProfileTab(val titleRes: Int) {
    POSTS(R.string.posts),
    SAVED(R.string.saved)
}