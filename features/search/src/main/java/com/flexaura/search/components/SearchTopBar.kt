package com.flexaura.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.core.designsystem.R

@Composable
internal fun SearchTopBar(
    query: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
    ) {
        FlexAuraTextField(
            query = query,
            onValueChange = { onValueChange(it) },
            onAction = { onSearchClicked() },
            placeholder = "Search",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(.7f)
                .fillMaxHeight()
        )
        IconButton(
            onClick = { onSearchClicked() },
            enabled = query.isNotBlank(),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                tint = if (query.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
            )
        }
    }
}