package com.flexaura.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    onDeletePost: ((postId: String) -> Unit)?,
    modifier: Modifier = Modifier,
    onAuthorClicked: (authorId: String) -> Unit = {}
) {
    val isAuthor = onDeletePost != null
    var showReadMore by remember { mutableStateOf(false) }
    var isTextExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .2f)
            ),
        shape = RoundedCornerShape(FlexAuraTheme.dimens.cornerRadiusLarge),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClicked(post.id) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingExtraSmall),
            modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
        ) {
            ProfileImage(
                imageModel = post.author?.imageUrl,
                onClick = { post.author?.id?.let(onAuthorClicked) }
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
                    Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingSmall))
                    Column {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = if (isTextExpanded) Int.MAX_VALUE else 8,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textLayoutResult ->
                                if(!isTextExpanded && textLayoutResult.hasVisualOverflow) {
                                    showReadMore = true
                                }
                            }
                        )
                        if (showReadMore) {
                            Text(
                                text = stringResource(
                                    if (isTextExpanded) R.string.see_less
                                    else R.string.read_more
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { isTextExpanded = !isTextExpanded}
                            )
                        }
                    }
                }

                post.imageUrl?.let {
                    Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingSmall))
                    PostImage(imageUrl = it, size = 250)
                }

                Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingSmall))

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