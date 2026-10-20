package com.flexaura.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme

@Composable
fun PaginationFooter(
    isLoading: Boolean,
    hasPaginationError: Boolean,
    onLoadMore: (isRetry: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(R.string.error_loading_posts)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            hasPaginationError -> Column(
                verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingExtraSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelMedium
                )
                Button(onClick = { onLoadMore(true) }) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }
    }
}