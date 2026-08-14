package com.flexaura.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flexaura.data.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    onAuthorClicked: (authorId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPostClicked(post.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(50f))
                    .background(Color.Red)
                    .size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            post.author?.let { author ->
                Text(
                    text = author.displayName,
                    modifier = Modifier.clickable { onAuthorClicked(author.id) }
                )
            }
        }

        post.content?.let {
            Text(text = it)
        }
        post.imageUrl?.let {
            Text(text = it)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (vote in -1..5) {
                    if (vote % 2 != 0) {
                        VoteButton(
                            vote = vote,
                            isSelected = vote == post.currentUserVote,
                            onClick = {
                                onVoteClicked(post.id, vote, post.currentUserVote)
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { onSavedClicked(post.id) }
            ) {
                Icon(
                    imageVector = if (post.isSavedByUser) Icons.Default.Check
                    else Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun VoteButton(
    vote: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        FilledTonalButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = vote.toString())
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = vote.toString())
        }
    }
}