package com.flexaura.components.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.PaginationFooter
import com.flexaura.components.general.PaginationState
import com.flexaura.components.utils.OnBottomReached
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.User

@Composable
fun ProfileList(
    paginationState: PaginationState<User>,
    onProfileClicked: (String) -> Unit,
    onLoadMore: (isRetry: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val users = paginationState.items

    listState.OnBottomReached(itemsCount = users.size) {
        if (
            !paginationState.isLoadingMore &&
            !paginationState.hasPaginationError &&
            !paginationState.endReached
        ) onLoadMore(false)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingLarge),
        modifier = modifier.fillMaxSize()
    ) {
        items(items = users, key = { it.id }) { user ->
            ProfileCard(
                user = user,
                onUserClicked = { userId ->
                    onProfileClicked(userId)
                }
            )
        }

        if (paginationState.isLoadingMore || paginationState.hasPaginationError) {
            item {
                PaginationFooter(
                    isLoading = paginationState.isLoadingMore,
                    hasPaginationError = paginationState.hasPaginationError,
                    onLoadMore = onLoadMore,
                    errorMessage = stringResource(R.string.error_loading_users),
                    modifier = Modifier
                        .padding(FlexAuraTheme.dimens.paddingExtraSmall)
                        .fillMaxWidth()
                )
            }
        }
    }
}