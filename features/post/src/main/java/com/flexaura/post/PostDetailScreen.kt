package com.flexaura.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.components.general.StateInfo
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.post.PostOptionsMenu
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.post.PostDetailUiEffect.NavigateBack
import com.flexaura.post.PostDetailUiEffect.NavigateToAuthorProfile
import com.flexaura.post.PostDetailUiEffect.ShowSnackBar
import com.flexaura.post.PostDetailUiIntent.Init
import com.flexaura.post.PostDetailUiIntent.OnCommentChanged
import com.flexaura.post.PostDetailUiIntent.OnDeletePost
import com.flexaura.post.PostDetailUiIntent.OnNavigateBack
import com.flexaura.post.PostDetailUiIntent.OnNavigateToAuthorProfile
import com.flexaura.post.PostDetailUiIntent.OnSavedClicked
import com.flexaura.post.PostDetailUiIntent.OnSubmitComment
import com.flexaura.post.PostDetailUiIntent.OnVoteClicked
import com.flexaura.post.components.CommentInputBar
import com.flexaura.post.components.CommentsSection
import com.flexaura.post.components.PostDetailContent
import com.flexaura.core.designsystem.R as DesignR

@Composable
internal fun PostRoute(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    viewModel: PostScreenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(postId) {
        viewModel.onIntent(Init(postId))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is NavigateBack -> onNavigateBack()
                is NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            val isCurrentUserPost = state.post?.author?.id == state.currentUserId

            NavigateBackTopBar(
                onNavigateBack = { viewModel.onIntent(OnNavigateBack) },
                title = "Post",
                trailingContent = {
                    if (isCurrentUserPost) {
                        PostOptionsMenu(
                            onDeletePost = { viewModel.onIntent(OnDeletePost(postId)) }
                        )
                    }
                },
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        bottomBar = {
            CommentInputBar(
                content = state.commentContent,
                onContentChanged = { viewModel.onIntent(OnCommentChanged(it)) },
                onSubmitComment = {
                    viewModel.onIntent(OnSubmitComment(postId, state.commentContent))
                },
                commentButtonEnabled = state.commentButtonEnabled
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { innerPadding ->
        PostDetailScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun PostDetailScreen(
    state: PostDetailUiState,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    when {
        state.isLoading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ){ CircularProgressIndicator() }

        state.errorMessage != null || state.post == null -> {
            val action = if (state.post?.id != null) ({ onIntent(Init(state.post.id)) }) else null

            val stateInfo = getPostDetailStateInfo(
                state = state,
                onAction = action
            )
            StateInfoScreen(
                stateInfo = stateInfo,
                modifier = Modifier.fillMaxSize()
            )
        }

        else -> Column(modifier = modifier.verticalScroll(scrollState)) {
            PostDetailContent(
                post = state.post,
                onVoteClicked = { postId, vote, currentVote ->
                    onIntent(OnVoteClicked(postId, vote, currentVote))
                },
                onSavedClicked = { postId ->
                    onIntent(OnSavedClicked(postId))
                },
                onAuthorClicked = { authorId ->
                    onIntent(OnNavigateToAuthorProfile(authorId))
                },
                modifier = Modifier.padding(FlexAuraTheme.dimens.paddingMedium)
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = FlexAuraTheme.dimens.paddingSmall),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(R.string.comments_section_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = FlexAuraTheme.dimens.paddingMedium)
            )

            CommentsSection(
                comments = state.comments,
                currentUserId = state.currentUserId,
                onNavigateToAuthorProfile = { authorId ->
                    onIntent(OnNavigateToAuthorProfile(authorId))
                },
                onDeleteComment = { commentId ->
                    onIntent(PostDetailUiIntent.OnDeleteComment(commentId))
                }
            )
        }
    }
}

@Composable
private fun getPostDetailStateInfo(
    state: PostDetailUiState,
    onAction: (() -> Unit)? = null
): StateInfo = when {
    state.errorMessage != null -> StateInfo(
        title = stringResource(DesignR.string.something_went_wrong),
        text = state.errorMessage,
        onActionClick = onAction,
        actionText = stringResource(DesignR.string.retry)
    )

    else -> StateInfo(title = stringResource(R.string.post_not_found_title))
}
