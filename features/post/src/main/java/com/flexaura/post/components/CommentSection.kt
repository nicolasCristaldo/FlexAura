package com.flexaura.post.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.flexaura.components.post.PostOptionsMenu
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.Comment

@Composable
internal fun CommentsSection(
    comments: List<Comment>,
    currentUserId: String?,
    onNavigateToAuthorProfile: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        comments.forEach { comment ->
            val isCurrentUserComment = comment.authorId == currentUserId
            Column {
                CommentCard(
                    comment = comment,
                    isCurrentUserComment = isCurrentUserComment,
                    onNavigateToAuthorProfile = onNavigateToAuthorProfile,
                    onDeleteComment = onDeleteComment,
                    modifier = Modifier
                        .padding(
                            vertical = FlexAuraTheme.dimens.paddingSmall,
                            horizontal = FlexAuraTheme.dimens.paddingMedium
                        )
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
    onNavigateToAuthorProfile: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReadMore by remember { mutableStateOf(false) }
    var isTextExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingExtraSmall),
        modifier = modifier
    ) {
        ProfileImage(
            imageModel = comment.authorAvatarUrl,
            onClick = { onNavigateToAuthorProfile(comment.authorId) },
            size = 44
        )
        Column(verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingSmall)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserIdentity(
                    displayName = comment.authorDisplayName,
                    username = comment.authorUsername,
                    onAuthorClicked = { onNavigateToAuthorProfile(comment.authorId) }
                )
                if (isCurrentUserComment) {
                    PostOptionsMenu(onDeletePost = { onDeleteComment(comment.id) })
                }
            }
            Text(
                comment.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isTextExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if(!isTextExpanded && textLayoutResult.hasVisualOverflow) {
                        showReadMore = true
                    }
                }
            )

            if (showReadMore) {
                Text(
                    text = stringResource(if (isTextExpanded) R.string.see_less else R.string.read_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { isTextExpanded = !isTextExpanded }
                )
            }
        }
    }
}