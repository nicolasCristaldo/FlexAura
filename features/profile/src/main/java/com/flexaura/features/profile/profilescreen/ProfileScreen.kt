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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostList
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.theme.appMainBackground
import com.flexaura.features.profile.profilescreen.ProfileUiEffect.*
import com.flexaura.features.profile.profilescreen.ProfileUiIntent.*
import com.flexaura.features.profile.profilescreen.components.ProfileDialog
import com.flexaura.features.profile.profilescreen.components.ProfileDialogType
import com.flexaura.features.profile.profilescreen.components.ProfileScreenHeader
import com.flexaura.features.profile.profilescreen.components.ProfileTopBar
import com.flexaura.features.profile.profilescreen.components.getDialogConfig
import kotlinx.coroutines.launch
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.profile.R as ProfileR

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
                is NavigateToAuth -> onLogout?.invoke()
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
                onLogOutClicked = { viewModel.handleIntent(ShowDialog(ProfileDialogType.LOGOUT)) },
                onDeleteAccountClicked = { viewModel.handleIntent(ShowDialog(ProfileDialogType.DELETE_ACCOUNT)) },
                modifier = Modifier.fillMaxHeight(.05f)
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
        when {
            state.isLoading && state.user == null -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }

            (state.errorMessage != null && state.user == null) || state.user == null-> StateInfoScreen(
                title = stringResource(ProfileR.string.error_loading_profile),
                actionText = stringResource(DesignR.string.retry),
                modifier = Modifier.fillMaxSize()
            ) { viewModel.handleIntent(LoadProfile) }

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

    if (state.dialogActive != null) {
        val dialogConfig = getDialogConfig(
            dialogType = state.dialogActive,
            onLogout = { onIntent(LogoutConfirmed) },
            onDeleteAccount = { onIntent(DeleteAccountConfirmed) },
            onDismiss = { onIntent(ShowDialog(null)) }
        )
        ProfileDialog(dialogConfig = dialogConfig)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        state.user?.let {
            ProfileScreenHeader(
                user = it,
                isOwnProfile = state.isOwnProfile,
                onToggleFollowClicked = { onIntent(OnToggleFollowClicked) },
                modifier = Modifier.padding(FlexAuraTheme.dimens.paddingMedium)
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = stringResource(tab.titleRes)) }
                    )
                }
            }

            HorizontalPager(state = pagerState) { pageIndex ->
                val currentTab = tabs[pageIndex]
                val paginationState = if (currentTab == ProfileTab.POSTS) state.posts else state.saved
                val isEmptyList = paginationState.isEmpty

                if((state.errorMessage != null && isEmptyList) || isEmptyList) {
                    val stateInfo = getTabStateInfo(
                        currentTab = currentTab,
                        isOwnProfile = state.isOwnProfile,
                        errorMessage = state.errorMessage,
                        onRetry = { onIntent(LoadProfile) }
                    )
                    StateInfoScreen(
                        stateInfo = stateInfo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else {
                    PostList(
                        paginationState = paginationState,
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
                        onLoadMore = { isRetry ->
                            if (currentTab == ProfileTab.POSTS) onIntent(LoadUserPosts(isRetry))
                            else onIntent(LoadSavedPosts(isRetry))
                        },
                        modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
                    )
                }
            }
        }
    }
}

@Composable
private fun getTabStateInfo(
    currentTab: ProfileTab,
    isOwnProfile: Boolean,
    errorMessage: String?,
    onRetry: (() -> Unit)? = null
): StateInfo = when {
    errorMessage != null -> StateInfo(
        title = stringResource(DesignR.string.something_went_wrong),
        text = errorMessage,
        onActionClick = onRetry,
        actionText = stringResource(DesignR.string.retry)
    )
    currentTab == ProfileTab.POSTS -> StateInfo(
        title = stringResource(
            if (isOwnProfile) ProfileR.string.profile_empty_posts_title
            else ProfileR.string.profile_empty_posts_desc
        ),
        text = if (isOwnProfile) stringResource(ProfileR.string.profile_empty_posts_own_desc) else null
    )
    else -> StateInfo(
        title = stringResource(ProfileR.string.profile_empty_saved_title),
        text = if (isOwnProfile) stringResource(ProfileR.string.profile_empty_saved_desc) else null
    )
}