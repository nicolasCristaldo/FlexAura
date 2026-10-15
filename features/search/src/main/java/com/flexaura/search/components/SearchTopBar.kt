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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.flexaura.components.general.FlexAuraInputField
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.search.R as SearchR
import com.flexaura.core.designsystem.R as DesignR

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
        FlexAuraInputField(
            query = query,
            onValueChange = { onValueChange(it) },
            onAction = { onSearchClicked() },
            placeholder = stringResource(SearchR.string.search),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            imeAction = ImeAction.Search,
            modifier = Modifier
                .padding(vertical = FlexAuraTheme.dimens.paddingExtraSmall)
                .fillMaxWidth(.7f)
                .fillMaxHeight()
        )
        IconButton(
            onClick = { onSearchClicked() },
            enabled = query.isNotBlank(),
            modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_search),
                contentDescription = stringResource(SearchR.string.search),
                tint = if (query.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
            )
        }
    }
}