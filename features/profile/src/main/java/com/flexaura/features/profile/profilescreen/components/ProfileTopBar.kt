package com.flexaura.features.profile.profilescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.features.profile.R

@Composable
internal fun ProfileTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (() -> Unit)?,
    onLogOutClicked: (() -> Unit)? = null
) {
    if(onNavigateToEditProfile != null && onLogOutClicked != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))
            IconButton(
                onClick = onNavigateToEditProfile,
                modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_profile_content_desc),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onLogOutClicked,
                modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.logout),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))
        }
    }
    else {
        NavigateBackTopBar(
            title = stringResource(R.string.profile_screen_title),
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}