package com.flexaura.post.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexaura.components.post.PostImage
import com.flexaura.components.post.PostInteractionButtons
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.data.domain.model.Post

@Composable
internal fun PostDetailContent(
    post: Post,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    onAuthorClicked: (authorId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileImage(
                imageModel = post.author?.imageUrl,
                onClick = { post.author?.id?.let(onAuthorClicked) },
                size = 56
            )

            UserIdentity(
                displayName = post.author?.displayName ?: "",
                username = post.author?.username ?: "",
                onAuthorClicked = { post.author?.id?.let(onAuthorClicked) },
                displayNameStyle = MaterialTheme.typography.titleLarge,
                usernameStyle = MaterialTheme.typography.bodyMedium
            )
        }

        post.content?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        post.imageUrl?.let {
            PostImage(imageUrl = it, size = 410)
        }

        PostInteractionButtons(
            post = post,
            onVoteClicked = onVoteClicked,
            onSavedClicked = onSavedClicked,
            onPostClicked = {},
            iconSize = 24
        )
    }
}