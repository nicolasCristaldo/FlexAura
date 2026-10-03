package com.flexaura.features.profile.profilescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flexaura.components.general.NavigateBackTopBar

@Composable
internal fun ProfileTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (() -> Unit)?
) {
    if(onNavigateToEditProfile != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateToEditProfile,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .8f)
                ),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit profile"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
    else {
        NavigateBackTopBar(
            title = "Profile",
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}