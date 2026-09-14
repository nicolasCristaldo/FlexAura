package com.flexaura.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.flexaura.core.designsystem.R

@Composable
fun ProfileImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = {}
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Profile photo",
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.ic_profile),
        placeholder = painterResource(R.drawable.ic_profile),
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick?.invoke() }
            .background(Color.Gray)
    )
}