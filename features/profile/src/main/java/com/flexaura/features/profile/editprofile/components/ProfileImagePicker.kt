package com.flexaura.features.profile.editprofile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.flexaura.components.user.ProfileImage
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.features.profile.R

@Composable
internal fun ProfileImagePicker(
    selectedImageUri: Any?,
    hasImage: Boolean,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        ProfileImage(
            imageModel = selectedImageUri,
            onClick = { showMenu = !showMenu },
            modifier = Modifier.size(FlexAuraTheme.dimens.profileImageSizeLarge)
        )

        IconButton(
            onClick = { showMenu = !showMenu },
            colors = IconButtonDefaults.iconButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .8f)
            ),
            modifier = Modifier
                .size(FlexAuraTheme.dimens.iconSizeLarge)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_profile_photo_content_desc),
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.select_image)) },
                onClick = {
                    showMenu = false
                    onPickImage()
                }
            )
            if (hasImage) DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.remove_image),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    showMenu = false
                    onRemoveImage()
                }
            )
        }
    }
}