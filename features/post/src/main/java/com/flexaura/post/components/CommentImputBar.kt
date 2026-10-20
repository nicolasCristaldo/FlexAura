package com.flexaura.post.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.FlexAuraInputField
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.post.R

@Composable
internal fun CommentInputBar(
    content: String,
    onContentChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    commentButtonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            .padding(horizontal = FlexAuraTheme.dimens.paddingMedium, vertical = FlexAuraTheme.dimens.paddingSmall)
    ) {
        FlexAuraInputField(
            query = content,
            onValueChange = { onContentChanged(it) },
            onAction = onSubmitComment,
            placeholder = stringResource(R.string.post_comment_input_placeholder),
            singleLine = false,
            maxLines = 2,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            modifier = Modifier
                .weight(1f)
                .height(FlexAuraTheme.dimens.commentBarHeight)
        )

        Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingMedium))

        IconButton(
            onClick = onSubmitComment,
            enabled = commentButtonEnabled,
            modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.send_comment_content_desc),
                tint = if (commentButtonEnabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}