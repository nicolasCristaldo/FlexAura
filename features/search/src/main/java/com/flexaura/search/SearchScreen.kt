package com.flexaura.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.PostCard
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.model.User
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when(effect) {
                is SearchUiEffect.ShowSnackBar -> snackBarHostState.showSnackbar(effect.message)
                is SearchUiEffect.NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
                is SearchUiEffect.NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePost
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        SearchScreen(
            state = state.value,
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

    LaunchedEffect(pagerState.currentPage) {
        onIntent(SearchUiIntent.OnTabChanged(SearchTab.entries[pagerState.currentPage]))
    }

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onIntent(SearchUiIntent.OnTextChange(it)) },
                label = { Text("Search") },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onIntent(SearchUiIntent.OnSearchClicked(state.query)) },
                enabled = state.query.isNotBlank()
            ) {
                Text("Search")
            }
        }

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
            if (state.isSearching && state.usersResults.isEmpty() && state.postsResults.isEmpty()) {
                CircularProgressIndicator()
            }
            else {
                when(SearchTab.entries[page]) {
                    SearchTab.PROFILES -> { UsersResultsList(users = state.usersResults) }
                    SearchTab.POSTS -> {
                        PostsResultsList(
                            posts = state.postsResults,
                            onIntent = onIntent
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun UsersResultsList(
    users: List<User>,
    modifier: Modifier = Modifier
) {
    if(users.isEmpty()) {
        Text(
            text = "No users found",
            modifier = modifier.fillMaxWidth()
        )
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                Text(text = user.username)
                Text(text = user.displayName)
                Text(text = user.bio ?: "")
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PostsResultsList(
    posts: List<Post>,
    onIntent: (SearchUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    if(posts.isEmpty()) {
        Text(
            text = "No posts found",
            modifier = modifier.fillMaxWidth()
        )
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onPostClicked = { postId ->
                        onIntent(SearchUiIntent.OnPostClicked(postId))
                    },
                    onVoteClicked = { postId, vote, currentVote ->
                        onIntent(SearchUiIntent.OnVoteClicked(postId, vote, currentVote))
                    },
                    onSavedClicked = { postId ->
                        onIntent(SearchUiIntent.OnSavedClicked(postId))
                    },
                    onAuthorClicked = { authorId ->
                        onIntent(SearchUiIntent.OnAuthorClicked(authorId))
                    }
                )
            }
        }
    }
}