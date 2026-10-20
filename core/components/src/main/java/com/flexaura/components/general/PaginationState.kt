package com.flexaura.components.general

data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,
    val hasPaginationError: Boolean = false,
    val endReached: Boolean = false
) {
    val isEmpty = items.isEmpty()
}
