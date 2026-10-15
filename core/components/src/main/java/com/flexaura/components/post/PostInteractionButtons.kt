package com.flexaura.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.negativeAuraColor
import com.flexaura.core.designsystem.theme.positiveAuraColor
import com.flexaura.core.designsystem.util.toCompactString
import com.flexaura.core.designsystem.util.toRelativeTimeSpan
import com.flexaura.data.domain.model.Post

@Composable
fun PostInteractionButtons(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Int = 20
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
            },
            size = iconSize
        )

        PostIcon(
            interactions = post.commentsCount,
            iconRes = R.drawable.ic_comment,
            contentDescription = stringResource(R.string.comments_content_desc),
            onClick = { onPostClicked(post.id) },
            size = iconSize
        )

        PostIcon(
            interactions = post.savesCount,
            iconRes = R.drawable.ic_bookmark,
            contentDescription = stringResource(R.string.saves_content_desc),
            tint = if (post.isSavedByUser) MaterialTheme.colorScheme.secondary.copy(alpha = .8f)
            else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { onSavedClicked(post.id) },
            size = iconSize
        )

        Text(
            text = post.createdAt.toRelativeTimeSpan(context = LocalContext.current),
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
    modifier: Modifier = Modifier,
    size: Int = 20
) {
    var expanded by remember { mutableStateOf(false) }
    val auraVotes = listOf(-1, 0, 1, 3, 5)

    Box(modifier = modifier) {
        PostIcon(
            interactions = totalAura,
            iconRes = if (totalAura >= 0) R.drawable.ic_flame else R.drawable.ic_skull,
            contentDescription = stringResource(R.string.aura_content_desc),
            onClick = { expanded = !expanded },
            tint = when (currentVote) {
                0 -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> {
                    if (totalAura > 0) positiveAuraColor else negativeAuraColor
                }
            },
            size = size
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    size: Int = 20
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = interactions.toCompactString(context = LocalContext.current),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(2.dp))

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(size.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}