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
import androidx.compose.ui.unit.dp
import com.flexaura.components.general.FlexAuraTextField

@Composable
fun CommentInputBar(
    content: String,
    onContentChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        FlexAuraTextField(
            query = content,
            onValueChange = { onContentChanged(it) },
            onAction = onSubmitComment,
            placeholder = "Write a comment...",
            singleLine = false,
            maxLines = 2,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(
            onClick = onSubmitComment,
            enabled = content.isNotBlank(),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send comment",
                tint = if (content.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}