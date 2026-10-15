package com.flexaura.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme

@Composable
fun NavigateBackTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.navigate_back_content_desc),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        title?.let { title ->
            Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        trailingContent?.let { icon ->
            icon()
            Spacer(modifier = Modifier.width(FlexAuraTheme.dimens.paddingSmall))
        }
    }
}