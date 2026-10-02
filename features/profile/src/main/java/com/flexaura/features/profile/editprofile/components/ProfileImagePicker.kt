package com.flexaura.features.profile.editprofile.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import com.flexaura.components.general.rememberPhotoPickerLauncher
import com.flexaura.components.user.ProfileImage

@Composable
internal fun ProfileImagePicker(
    selectedImageUri: Any?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val launchPhotoPicker = rememberPhotoPickerLauncher(onImageSelected = onImageSelected)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        ProfileImage(
            imageModel = selectedImageUri,
            onClick = launchPhotoPicker,
            modifier = Modifier.size(120.dp)
        )

        IconButton(
            onClick = launchPhotoPicker,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = .8f),
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit profile photo",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}