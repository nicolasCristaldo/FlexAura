package com.flexaura.post.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexaura.components.post.PostOptionsMenu
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.data.domain.model.Comment
import com.flexaura.post.PostDetailUiIntent
import com.flexaura.post.PostDetailUiIntent.OnDeleteComment
import com.flexaura.post.PostDetailUiIntent.OnNavigateToAuthorProfile
import com.flexaura.post.PostDetailUiState

@Composable
internal fun CommentsSection(
    state: PostDetailUiState,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = state.comments, key = { it.id }) { comment ->
            val isCurrentUserComment = comment.authorId == state.currentUserId

            Column {
                CommentCard(
                    comment = comment,
                    isCurrentUserComment = isCurrentUserComment,
                    onIntent = onIntent,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: Comment,
    isCurrentUserComment: Boolean,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        ProfileImage(
            imageModel = comment.authorAvatarUrl,
            onClick = { onIntent(OnNavigateToAuthorProfile(comment.authorId)) },
            size = 44
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserIdentity(
                    displayName = comment.authorDisplayName,
                    username = comment.authorUsername,
                    onAuthorClicked = { onIntent(OnNavigateToAuthorProfile(comment.authorId)) }
                )
                if (isCurrentUserComment) {
                    PostOptionsMenu(onDeletePost = { onIntent(OnDeleteComment(comment.id)) })
                }
            }
            Text(
                comment.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}