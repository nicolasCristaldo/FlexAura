package com.flexaura.features.profile.profilescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.ConfirmationDialog
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostList
import com.flexaura.core.designsystem.theme.appMainBackground
import com.flexaura.features.profile.profilescreen.components.ProfileScreenHeader
import com.flexaura.features.profile.profilescreen.components.ProfileTopBar
import com.flexaura.features.profile.profilescreen.ProfileUiEffect.NavigateToAuthorProfile
import com.flexaura.features.profile.profilescreen.ProfileUiEffect.NavigateToPostDetail
import com.flexaura.features.profile.profilescreen.ProfileUiEffect.ShowSnackBar
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnAuthorClicked
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnDeletePostClicked
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnPostClicked
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnSavedClicked
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnToggleFollowClicked
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.OnVoteClicked
import kotlinx.coroutines.launch

@Composable
internal fun ProfileRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> snackBarHostState.showSnackbar(effect.message)
                is NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
                is NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
                is ProfileUiEffect.NavigateToAuth -> onLogout?.invoke()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ProfileTopBar(
                onNavigateBack = onNavigateBack,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onLogOutClicked = { viewModel.handleIntent(ProfileUiIntent.ShowLogoutDialog())},
                modifier = Modifier.fillMaxHeight(.05f)
            )
        },
        floatingActionButton = { FlexAuraFab(onClick = onNavigateToCreatePost) },
        containerColor = Color.Transparent,
        modifier = Modifier.appMainBackground()
    ) { innerPadding ->
        when {
            state.isLoading && state.user == null -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }

            (state.errorMessage != null && state.user == null) || state.user == null-> StateInfoScreen(
                title = "Error loading profile",
                modifier = Modifier.fillMaxSize()
            ) { viewModel.handleIntent(ProfileUiIntent.Refresh) }

            else -> ProfileScreen(
                state = state,
                onIntent = viewModel::handleIntent,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onIntent: (ProfileUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember(state.isOwnProfile) {
        if (state.isOwnProfile) listOf(ProfileTab.POSTS, ProfileTab.SAVED)
        else listOf(ProfileTab.POSTS)
    }
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    if (state.showDialog) {
        ConfirmationDialog(
            title = "Log out",
            text = "Are you sure you want to log out?",
            onConfirm = { onIntent(ProfileUiIntent.LogoutConfirmed) },
            onDismiss = { onIntent(ProfileUiIntent.ShowLogoutDialog(false)) },
            confirmText = "Log out"
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        state.user?.let {
            ProfileScreenHeader(
                user = it,
                isOwnProfile = state.isOwnProfile,
                onToggleFollowClicked = { onIntent(OnToggleFollowClicked) },
                modifier = Modifier.padding(12.dp)
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = tab.title) }
                    )
                }
            }

            HorizontalPager(state = pagerState) { pageIndex ->
                val currentTab = tabs[pageIndex]
                val posts = if (currentTab == ProfileTab.POSTS) state.posts else state.savedPosts
                val isEmptyList = posts.isEmpty()

                if((state.errorMessage != null && isEmptyList) || isEmptyList) {
                    val stateInfo = getTabStateInfo(
                        currentTab = currentTab,
                        isOwnProfile = state.isOwnProfile,
                        errorMessage = state.errorMessage,
                        onRetry = { onIntent(ProfileUiIntent.Refresh) }
                    )
                    StateInfoScreen(
                        stateInfo = stateInfo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else {
                    PostList(
                        posts = posts,
                        currentUserId = state.currentUserId,
                        onPostClicked = { postId -> onIntent(OnPostClicked(postId)) },
                        onVoteClicked = { postId, vote, currentVote ->
                            onIntent(OnVoteClicked(postId, vote, currentVote))
                        },
                        onSavedClicked = { postId -> onIntent(OnSavedClicked(postId)) },
                        onDeletePost = { postId ->
                            onIntent(OnDeletePostClicked(postId))
                        },
                        onAuthorClicked = { authorId ->
                            if (state.user.id != authorId) onIntent(OnAuthorClicked(authorId))
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

private fun getTabStateInfo(
    currentTab: ProfileTab,
    isOwnProfile: Boolean,
    errorMessage: String?,
    onRetry: (() -> Unit)? = null
): StateInfo = when {
    errorMessage != null -> StateInfo(
        title = "Something went wrong",
        text = errorMessage,
        onActionClick = onRetry
    )
    currentTab == ProfileTab.POSTS -> StateInfo(
        title = if (isOwnProfile) "No posts found" else "This user has no posts",
        text = if (isOwnProfile) "Create a post to see it here." else null
    )
    else -> StateInfo(
        title = if (isOwnProfile) "No saved posts found" else "This user has no saved posts",
        text = if (isOwnProfile) "Save a post to see it here." else null
    )
}