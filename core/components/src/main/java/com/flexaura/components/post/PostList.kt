package com.flexaura.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.Post

@Composable
fun PostList(
    posts: List<Post>,
    currentUserId: String?,
    onPostClicked: (String) -> Unit,
    onVoteClicked: (String, Int, Int) -> Unit,
    onSavedClicked: (String) -> Unit,
    onAuthorClicked: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
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
    }
}