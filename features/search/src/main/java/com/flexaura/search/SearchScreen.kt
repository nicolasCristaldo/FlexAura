package com.flexaura.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostList
import com.flexaura.components.user.ProfileList
import com.flexaura.core.designsystem.theme.appMainBackground
import com.flexaura.search.SearchUiEffect.*
import com.flexaura.search.SearchUiIntent.*
import com.flexaura.search.components.SearchTopBar
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
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
        floatingActionButton = { FlexAuraFab(onClick = onNavigateToCreatePost) },
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
fun SearchScreen(
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
            val isProfilesEmpty = currentTab == SearchTab.PROFILES && state.usersResults.isEmpty()
            val isPostsEmpty = currentTab == SearchTab.POSTS && state.postsResults.isEmpty()
            val showResults = !isProfilesEmpty && !isPostsEmpty && state.hasSearched && state.errorMessage == null

            when {
                state.isSearching -> CircularProgressIndicator()
                !showResults -> {
                    val stateInfo = getSearchStateInfo(
                        state = state,
                        currentTab = currentTab,
                        onRetry = { onIntent(Refresh) }
                    )

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
                        modifier = Modifier.padding(8.dp)
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
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

private fun getSearchStateInfo(
    state: SearchUiState,
    currentTab: SearchTab,
    onRetry: (() -> Unit)? = null
): StateInfo = when {
    state.errorMessage != null -> StateInfo(
        title = "Something went wrong",
        text = state.errorMessage,
        onActionClick = onRetry
    )
    !state.hasSearched -> StateInfo(
        title = "Search for something!",
        text = "Find amazing post and profiles to connect with."
    )
    currentTab == SearchTab.PROFILES -> StateInfo(
        title = "No users found",
        text = "Try searching for something else."
    )
    else -> StateInfo(
        title = "No posts found",
        text = "Try searching for something else."
    )
}