package com.flexaura.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostList
import com.flexaura.components.user.ProfileList
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.theme.appMainBackground
import com.flexaura.search.SearchUiEffect.NavigateToAuthorProfile
import com.flexaura.search.SearchUiEffect.NavigateToPostDetail
import com.flexaura.search.SearchUiEffect.ShowSnackBar
import com.flexaura.search.SearchUiIntent.OnAuthorClicked
import com.flexaura.search.SearchUiIntent.OnDeletePostClicked
import com.flexaura.search.SearchUiIntent.OnPostClicked
import com.flexaura.search.SearchUiIntent.OnSavedClicked
import com.flexaura.search.SearchUiIntent.OnSearchClicked
import com.flexaura.search.SearchUiIntent.OnTextChange
import com.flexaura.search.SearchUiIntent.OnVoteClicked
import com.flexaura.search.SearchUiIntent.Refresh
import com.flexaura.search.components.SearchTopBar
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.search.R as SearchR
import kotlinx.coroutines.launch

@Composable
internal fun SearchRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> snackBarHostState.showSnackbar(effect.message)
                is NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
                is NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SearchTopBar(
                query = state.query,
                onValueChange = {
                    viewModel.onIntent(OnTextChange(it))
                },
                onSearchClicked = {
                    viewModel.onIntent(OnSearchClicked(state.query))
                },
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        floatingActionButton = {
            FlexAuraFab(
                onClick = onNavigateToCreatePost,
                contentDescription = stringResource(DesignR.string.create_post_content_desc)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appMainBackground()
    ) { innerPadding ->
        SearchScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SearchScreen(
    state: SearchUiState,
    onIntent: (SearchUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { SearchTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            SearchTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val currentTab = SearchTab.entries[page]
            val results = if (currentTab == SearchTab.PROFILES) state.usersResults else state.postsResults
            val isEmptyList = results.isEmpty()
            val showStateInfoScreen = (state.errorMessage != null && isEmptyList) || isEmptyList || !state.hasSearched

            when {
                state.isSearching -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ){ CircularProgressIndicator() }
                showStateInfoScreen -> {
                    val stateInfo = getSearchStateInfo(state = state, currentTab = currentTab) {
                        onIntent(Refresh)
                    }
                    StateInfoScreen(
                        stateInfo = stateInfo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    if (currentTab == SearchTab.PROFILES) ProfileList(
                        users = state.usersResults,
                        onProfileClicked = { userId ->
                            onIntent(OnAuthorClicked(userId))
                        },
                        modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
                    )
                    else PostList(
                        posts = state.postsResults,
                        currentUserId = state.currentUserId,
                        onPostClicked = { postId ->
                            onIntent(OnPostClicked(postId))
                        },
                        onVoteClicked = { postId, vote, currentVote ->
                            onIntent(OnVoteClicked(postId, vote, currentVote))
                        },
                        onSavedClicked = { postId ->
                            onIntent(OnSavedClicked(postId))
                        },
                        onAuthorClicked = { authorId ->
                            onIntent(OnAuthorClicked(authorId))
                        },
                        onDeletePost = { postId ->
                            onIntent(OnDeletePostClicked(postId))
                        },
                        modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
                    )
                }
            }
        }
    }
}

@Composable
private fun getSearchStateInfo(
    state: SearchUiState,
    currentTab: SearchTab,
    onRetry: (() -> Unit)? = null
): StateInfo = when {
    state.errorMessage != null -> StateInfo(
        title = stringResource(SearchR.string.search_error_title),
        text = state.errorMessage,
        onActionClick = onRetry,
        actionText = stringResource(DesignR.string.retry)
    )
    !state.hasSearched -> StateInfo(
        title = stringResource(SearchR.string.search_empty_start_title),
        text = stringResource(SearchR.string.search_empty_start_desc)
    )
    currentTab == SearchTab.PROFILES -> StateInfo(
        title = stringResource(SearchR.string.search_no_results_users_title),
        text = stringResource(SearchR.string.search_no_results_users_desc)
    )
    else -> StateInfo(
        title = stringResource(SearchR.string.search_no_results_posts_title),
        text = stringResource(SearchR.string.search_no_results_posts_desc)
    )
}