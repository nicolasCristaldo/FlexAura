package com.flexaura.components.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LazyListState.OnBottomReached(
    itemsCount: Int,
    buffer: Int = 3,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(this, itemsCount) {
        snapshotFlow {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= itemsCount - buffer && itemsCount > 0
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if(shouldLoadMore) onLoadMore()
            }
    }
}