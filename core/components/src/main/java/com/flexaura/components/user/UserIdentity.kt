package com.flexaura.components.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun UserIdentity(
    displayName: String,
    username: String,
    modifier: Modifier = Modifier,
    onAuthorClicked: (() -> Unit)? = {},
    displayNameStyle: TextStyle = MaterialTheme.typography.titleMedium,
    usernameStyle: TextStyle = MaterialTheme.typography.bodySmall
) {
    Column(
        modifier = modifier.clickable { onAuthorClicked?.invoke() }
    ) {
        Text(
            text = displayName,
            style = displayNameStyle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "@$username",
            style = usernameStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}