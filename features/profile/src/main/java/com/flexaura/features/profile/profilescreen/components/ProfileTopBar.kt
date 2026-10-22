package com.flexaura.features.profile.profilescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.features.profile.R

@Composable
internal fun ProfileTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (() -> Unit)?,
    onLogOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit
) {
    if(onNavigateToEditProfile != null) {
        OwnProfileTopBar(
            onNavigateToEditProfile = onNavigateToEditProfile,
            onLogOutClicked = onLogOutClicked,
            onDeleteAccountClicked = onDeleteAccountClicked,
            modifier = modifier
        )
    }
    else {
        NavigateBackTopBar(
            title = stringResource(R.string.profile_screen_title),
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}

@Composable
private fun OwnProfileTopBar(
    modifier: Modifier = Modifier,
    onNavigateToEditProfile: () -> Unit,
    onLogOutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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

        Box {
            IconButton(
                onClick = { showMenu = !showMenu },
                modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.options_content_desc),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.logout))
                    },
                    onClick = {
                        showMenu = false
                        onLogOutClicked()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.delete_account),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDeleteAccountClicked()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))
    }
}