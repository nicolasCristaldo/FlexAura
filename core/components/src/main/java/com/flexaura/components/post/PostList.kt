package com.flexaura.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flexaura.components.general.PaginationFooter
import com.flexaura.components.general.PaginationState
import com.flexaura.components.utils.OnBottomReached
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.Post

@Composable
fun PostList(
    paginationState: PaginationState<Post>,
    currentUserId: String?,
    onPostClicked: (String) -> Unit,
    onVoteClicked: (String, Int, Int) -> Unit,
    onSavedClicked: (String) -> Unit,
    onAuthorClicked: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onLoadMore: (isRetry: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val posts = paginationState.items

    listState.OnBottomReached(itemsCount = posts.size) {
        if (
            !paginationState.isLoadingMore &&
            !paginationState.hasPaginationError &&
            !paginationState.endReached
        ) onLoadMore(false)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingLarge),
        modifier = modifier.fillMaxSize()
    ) {
        items(items = posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onPostClicked = { postId ->
                    onPostClicked(postId)
                },
                onVoteClicked = { postId, vote, currentVote ->
                    onVoteClicked(postId, vote, currentVote)
                },
                onSavedClicked = { postId ->
                    onSavedClicked(postId)
                },
                onAuthorClicked = { authorId ->
                    onAuthorClicked(authorId)
                },
                onDeletePost = if (post.author?.id == currentUserId) { postId ->
                    onDeletePost(postId)
                } else null
            )
        }

        if (paginationState.isLoadingMore || paginationState.hasPaginationError) {
            item {
                PaginationFooter(
                    isLoading = paginationState.isLoadingMore,
                    hasPaginationError = paginationState.hasPaginationError,
                    onLoadMore = onLoadMore,
                    modifier = Modifier
                        .padding(FlexAuraTheme.dimens.paddingExtraSmall)
                        .fillMaxWidth()
                )
            }
        }
    }
}