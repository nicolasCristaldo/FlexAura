package com.flexaura.features.profile.profilescreen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
internal fun FollowButton(
    onToggleFollow: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleFollow,
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier.size(18.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription
        )
    }
}