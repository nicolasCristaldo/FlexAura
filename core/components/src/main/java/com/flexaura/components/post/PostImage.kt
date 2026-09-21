package com.flexaura.components.post

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PostImage(
    imageUrl: String?,
    size: Int,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Post image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .heightIn(max = size.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}