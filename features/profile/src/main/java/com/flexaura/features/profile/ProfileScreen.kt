package com.flexaura.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.PostCard

@Composable
fun ProfileRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ProfileUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is ProfileUiEffect.NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        topBar = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
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
        ProfileScreen(
            state = state.value,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onIntent: (ProfileUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    if(state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) { CircularProgressIndicator() }
    }
    else {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Text("Display Name: ${state.user?.displayName ?: "Unknown"}")
            Text("Username: ${state.user?.username ?: "Unknown"}")
            Text("Bio: ${state.user?.bio ?: "No bio"}")
            Text("Followers: ${state.user?.followersCount}")
            Text("Following: ${state.user?.followingCount}")
            if(!state.isOwnProfile) {
                Button(
                    onClick = { onIntent(ProfileUiIntent.OnToggleFollowClicked) }
                ) {
                    Text(text = if (state.user?.isFollowedByCurrentUser == true) "Unfollow" else "Follow")
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.posts) { post ->
                    PostCard(
                        post = post,
                        onPostClicked = { postId ->
                            onIntent(ProfileUiIntent.OnPostClicked(postId))
                        },
                        onVoteClicked = { postId, vote, currentVote ->
                            onIntent(ProfileUiIntent.OnVoteClicked(postId, vote, currentVote))
                        },
                        onSavedClicked = { postId ->
                            onIntent(ProfileUiIntent.OnSavedClicked(postId))
                        }
                    )
                }
            }
        }
    }
}