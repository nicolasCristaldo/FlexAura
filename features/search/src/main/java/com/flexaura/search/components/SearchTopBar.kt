package com.flexaura.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
        SearchTextField(
            query = query,
            onValueChange = { onValueChange(it) },
            onSearchClicked = onSearchClicked,
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
                tint = if (query.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
            )
        }
    }
}

@Composable
private fun SearchTextField(
    query: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    containerColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .background(color = containerColor, shape = shape)
            .padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.weight(1f)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = { onValueChange(it) },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { if (query.isNotBlank()) onSearchClicked() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search text",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onValueChange("") }
                )
            }
        }
    }
}