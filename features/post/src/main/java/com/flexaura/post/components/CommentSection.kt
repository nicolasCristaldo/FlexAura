package com.flexaura.post.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexaura.data.domain.model.Comment
import com.flexaura.post.PostDetailUiIntent
import com.flexaura.post.PostDetailUiIntent.*
import com.flexaura.post.PostDetailUiState

@Composable
fun CommentsSection(
    state: PostDetailUiState,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = state.comments, key = { it.id }) { comment ->
            val isCurrentUserComment = comment.authorId == state.currentUserId

            CommentCard(
                comment = comment,
                isCurrentUserComment = isCurrentUserComment,
                onIntent = onIntent
            )
        }
    }
}

@Composable
fun CommentCard(
    comment: Comment,
    isCurrentUserComment: Boolean,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                comment.authorDisplayName,
                modifier = Modifier.clickable { onIntent(OnNavigateToAuthorProfile(comment.authorId)) }
            )
            Text(comment.content)
        }

        if (isCurrentUserComment) {
            IconButton(onClick = { onIntent(OnDeleteComment(comment.id)) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }
    }
}