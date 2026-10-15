package com.flexaura.createpost.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.createpost.R

@Composable
internal fun SelectedImagePreview(
    imageUri: Uri,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = imageUri,
            contentDescription = stringResource(R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(FlexAuraTheme.dimens.paddingSmall))
        )
        IconButton(
            onClick = onRemoveImage,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.DarkGray.copy(alpha = .8f)
            ),
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.TopEnd)
                .padding(FlexAuraTheme.dimens.paddingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = Color.Black,
                contentDescription = stringResource(R.string.remove_image)
            )
        }
    }
}