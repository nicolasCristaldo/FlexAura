package com.flexaura.post

import com.flexaura.data.domain.model.Comment
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.util.isValidLength

/**
 * Sealed interface representing user actions and intents triggered from the post detail screen.
 */
internal sealed interface PostDetailUiIntent {
    /**
     * Intent triggered to initialize the screen with a specific post ID.
     *
     * @property postId The unique identifier of the post.
     */
    data class Init(val postId: String) : PostDetailUiIntent

    /**
     * Intent triggered when the user casts or updates a vote on the post.
     *
     * @property postId The unique identifier of the post being voted on.
     * @property vote The target vote value.
     * @property currentVote The user's current vote value prior to this action.
     */
    data class OnVoteClicked(val postId: String, val vote: Int, val currentVote: Int) : PostDetailUiIntent

    /**
     * Intent triggered when the user clicks the save/bookmark action on the post.
     *
     * @property postId The unique identifier of the post.
     */
    data class OnSavedClicked(val postId: String) : PostDetailUiIntent

    /**
     * Intent triggered when the user modifies the comment input text.
     *
     * @property content The updated comment input string.
     */
    data class OnCommentChanged(val content: String) : PostDetailUiIntent

    /**
     * Intent triggered when the user submits a new comment on the post.
     *
     * @property postId The unique identifier of the target post.
     * @property content The text content of the comment.
     */
    data class OnSubmitComment(val postId: String, val content: String) : PostDetailUiIntent

    /**
     * Intent triggered when the user requests to delete a comment.
     *
     * @property commentId The unique identifier of the comment to delete.
     */
    data class OnDeleteComment(val commentId: String) : PostDetailUiIntent

    /**
     * Intent triggered when the user requests to delete the post.
     *
     * @property postId The unique identifier of the post to delete.
     */
    data class OnDeletePost(val postId: String) : PostDetailUiIntent

    /** Intent triggered when the user navigates back to the previous screen. */
    data object OnNavigateBack : PostDetailUiIntent

    /**
     * Intent triggered when the user clicks on the author's profile.
     *
     * @property authorId The unique identifier of the author.
     */
    data class OnNavigateToAuthorProfile(val authorId: String) : PostDetailUiIntent
}

/**
 * Data class representing the UI state of the post detail screen.
 *
 * @property post The loaded domain [Post] details, or `null` if loading.
 * @property currentUserId The unique identifier of the currently authenticated user, or `null`.
 * @property commentContent The current comment text input value.
 * @property comments List of domain [Comment] items associated with the post.
 * @property isLoading Flag indicating whether the post is currently being fetched.
 * @property errorMessage Optional error message describing a failure, or `null`.
 * @property commentButtonEnabled Computed property indicating whether the comment submission button is active.
 */
internal data class PostDetailUiState(
    val post: Post? = null,
    val currentUserId: String? = null,
    val commentContent: String = "",
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val commentButtonEnabled = commentContent.isValidLength(1, 280)
}

/**
 * Sealed interface representing one-off side effects emitted by the [PostScreenViewModel] to the UI.
 */
internal sealed interface PostDetailUiEffect {
    /**
     * Effect requesting the display of a snackbar notification.
     *
     * @property message The text message to display in the snackbar.
     */
    data class ShowSnackBar(val message: String) : PostDetailUiEffect

    /** Effect requesting navigation back to the previous screen. */
    data object NavigateBack : PostDetailUiEffect

    /**
     * Effect requesting navigation to an author's profile screen.
     *
     * @property authorId The unique identifier of the author.
     */
    data class NavigateToAuthorProfile(val authorId: String) : PostDetailUiEffect
}