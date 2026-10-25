package com.flexaura.components.general

/**
 * Represents the UI state for paginated lists, managing items, page tracking, loading states, errors, and boundaries.
 *
 * @param T The type of items contained in the paginated list.
 * @property items The current list of loaded items.
 * @property currentPage The current page index for the next pagination request.
 * @property isLoadingMore Flag indicating whether additional items are currently being fetched.
 * @property hasPaginationError Flag indicating whether an error occurred during pagination loading.
 * @property endReached Flag indicating whether all available items have been fetched (no more pages left).
 */
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,
    val hasPaginationError: Boolean = false,
    val endReached: Boolean = false
) {
    /**
     * Checks if the list of items is empty.
     */
    val isEmpty = items.isEmpty()
}
