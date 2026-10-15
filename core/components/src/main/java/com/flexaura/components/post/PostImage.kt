package com.flexaura.components.post

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme

@Composable
fun PostImage(
    imageUrl: String?,
    size: Int,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.post_image_content_desc),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .heightIn(max = size.dp)
            .clip(RoundedCornerShape(FlexAuraTheme.dimens.cornerRadiusSmall))
    )
}