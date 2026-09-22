package com.flexaura.components.general

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flexaura.core.designsystem.R

@Composable
fun FlexAuraFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_pen,
    contentDescription: String = "Create post"
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(34.dp)
        )
    }
}