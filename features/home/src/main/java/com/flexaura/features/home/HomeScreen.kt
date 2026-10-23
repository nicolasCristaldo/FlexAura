package com.flexaura.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostList
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.theme.appMainBackground
import com.flexaura.features.home.HomeUiEffect.*
import com.flexaura.features.home.HomeUiIntent.*
import com.flexaura.features.home.components.HomeTopAppBar
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.home.R as HomeR
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
                is NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopAppBar(
                onClick = { viewModel.handleIntent(Refresh) },
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        floatingActionButton = {
            FlexAuraFab(
                onClick = onNavigateToCreatePost,
                contentDescription = stringResource(DesignR.string.create_post_content_desc),
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appMainBackground()
    ) { innerPadding ->
        HomeScreen(
            state = state.value,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { HomeTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            HomeTab.entries.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(stringResource(tab.titleRes)) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentTab = HomeTab.entries[page]
            val paginationState = if (currentTab == HomeTab.DISCOVER) state.discover else state.following
            val isEmptyList = paginationState.isEmpty

            when {
                state.isLoading && isEmptyList -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ){ CircularProgressIndicator() }

                (state.errorMessage != null && isEmptyList) || isEmptyList -> {
                    val stateInfo = getHomeStateInfo(state = state, currentTab = currentTab) {
                        onIntent(Refresh)
                    }
                    StateInfoScreen(
                        stateInfo = stateInfo,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> PostList(
                    paginationState = paginationState,
                    currentUserId = state.currentUserId,
                    onPostClicked = { postId ->
                        onIntent(OnPostClicked(postId))
                    },
                    onVoteClicked = { postId, vote, currentVote ->
                        onIntent(OnVoteClicked(postId, vote, currentVote))
                    },
                    onSavedClicked = {
                        postId -> onIntent(OnSavedClicked(postId))
                    },
                    onAuthorClicked = { authorId ->
                        onIntent(OnAuthorClicked(authorId))
                    },
                    onDeletePost = { postId ->
                        onIntent(OnDeletePostClicked(postId))
                    },
                    onLoadMore = { isRetry ->
                        if (currentTab == HomeTab.DISCOVER) onIntent(LoadDiscoverPosts(isRetry))
                        else onIntent(LoadFollowingPosts(isRetry))
                    },
                    modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
                )
            }
        }
    }
}

@Composable
private fun getHomeStateInfo(
    state: HomeUiState,
    currentTab: HomeTab,
    onRetry: (() -> Unit)? = null
): StateInfo = when {
    state.errorMessage != null -> StateInfo(
        title = stringResource(DesignR.string.something_went_wrong),
        text = state.errorMessage,
        onActionClick = onRetry,
        actionText = stringResource(DesignR.string.retry)
    )
    currentTab == HomeTab.DISCOVER -> StateInfo(title = stringResource(DesignR.string.no_posts_found))
    else -> StateInfo(
        title = stringResource(HomeR.string.empty_following_title),
        text = stringResource(HomeR.string.empty_following_desc)
    )
}