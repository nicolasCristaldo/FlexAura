package com.flexaura.components.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Observes the scroll position of a [LazyListState] and triggers a callback when the user scrolls
 * near the bottom of the list, facilitating infinite scrolling and pagination.
 *
 * @param itemsCount The total number of items currently present in the list.
 * @param buffer The number of items away from the end of the list that will trigger the load more action. Defaults to 3.
 * @param onLoadMore Callback invoked when the threshold is reached and more data should be fetched.
 */
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