package com.flexaura.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.flexaura.components.general.ConfirmationDialog
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.negativeAuraColor
import com.flexaura.core.designsystem.theme.positiveAuraColor
import com.flexaura.core.designsystem.util.toCompactString
import com.flexaura.core.designsystem.util.toRelativeTimeSpan
import com.flexaura.data.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    onDeletePost: ((postId: String) -> Unit)?,
    onAuthorClicked: (authorId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAuthor = onDeletePost != null

    Card(
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .2f)
            ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClicked(post.id) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            ProfileImage(
                imageUrl = post.author?.imageUrl,
                onClick = { post.author?.id?.let(onAuthorClicked) },
                modifier = Modifier.size(48.dp)
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserIdentity(
                        displayName = post.author?.displayName ?: "",
                        username = post.author?.username ?: "",
                        onAuthorClicked = { post.author?.id?.let(onAuthorClicked) }
                    )
                    if (isAuthor) {
                        PostOptionsMenu(onDeletePost = { onDeletePost(post.id) })
                    }
                }

                post.content?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                post.imageUrl?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = it,
                        contentDescription = "Post image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PostInteractionButtons(
                    post = post,
                    onPostClicked = onPostClicked,
                    onVoteClicked = onVoteClicked,
                    onSavedClicked = onSavedClicked
                )
            }
        }
    }
}

@Composable
private fun PostOptionsMenu(
    onDeletePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options",
            modifier = Modifier
                .size(18.dp)
                .clickable { showMenu = !showMenu }
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    showDeleteDialog = true
                    showMenu = false
                }
            )
        }

        if (showDeleteDialog) {
            ConfirmationDialog(
                title = "Delete Post",
                text = "Are you sure you want to delete this post? This action cannot be undone.",
                onConfirm = {
                    showDeleteDialog = false
                    onDeletePost()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
private fun PostInteractionButtons(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        ExpandableIconVote(
            totalAura = post.totalAura,
            currentVote = post.currentUserVote,
            onValueSelected = { vote ->
                onVoteClicked(post.id, vote, post.currentUserVote)
            }
        )

        PostIcon(
            interactions = post.commentsCount,
            iconRes = R.drawable.ic_comment,
            contentDescription = "Comments",
            onClick = { onPostClicked(post.id) }
        )

        PostIcon(
            interactions = post.savesCount,
            iconRes = R.drawable.ic_bookmark,
            contentDescription = "Saves",
            tint = if (post.isSavedByUser) MaterialTheme.colorScheme.secondary.copy(alpha = .8f)
            else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { onSavedClicked(post.id) }
        )

        Text(
            text = post.createdAt.toRelativeTimeSpan(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExpandableIconVote(
    currentVote: Int,
    totalAura: Int,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val auraVotes = listOf(-1, 0, 1, 3, 5)

    Box(modifier = modifier) {
        PostIcon(
            interactions = totalAura,
            iconRes = if (totalAura >= 0) R.drawable.ic_flame else R.drawable.ic_skull,
            contentDescription = "Aura",
            onClick = { expanded = !expanded },
            tint = when (currentVote) {
                0 -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> {
                    if (totalAura > 0) positiveAuraColor else negativeAuraColor
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                auraVotes.forEach { vote ->
                    IconButton(
                        onClick = {
                            onValueSelected(vote)
                            expanded = false
                        }
                    ) {
                        Text(
                            text = if (vote > 0) "+$vote" else "$vote",
                            fontWeight = if (currentVote == vote) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentVote == vote) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostIcon(
    interactions: Int,
    iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit = {},
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = interactions.toCompactString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(2.dp))

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}