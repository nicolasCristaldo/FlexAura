package com.flexaura.features.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.data.domain.model.Post
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.ui.Alignment

@Composable
fun ProfileRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
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
            Button(
                onClick = { onIntent(ProfileUiIntent.OnToggleFollowClicked) }
            ) {
                Text(text = if (state.user?.isFollowedByCurrentUser == true) "Unfollow" else "Follow")
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

@Composable
fun PostCard(
    post: Post,
    onPostClicked: (postId: String) -> Unit,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .clickable { onPostClicked(post.id) }
    ) {
        post.content?.let {
            Text(text = it)
        }
        post.imageUrl?.let {
            Text(text = it)
        }

        Text(text = "Votes: ${post.totalAura}")
        Text(text = "Saves: ${post.savesCount}")

        Row {
            Button(onClick = { onVoteClicked(post.id, 1, post.currentUserVote) }) {
                Text("+1")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onVoteClicked(post.id, -1, post.currentUserVote) }) {
                Text("-1")
            }
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = { onSavedClicked(post.id) }
            ) {
                Icon(
                    imageVector = if(post.isSavedByUser) Icons.Default.Check
                    else Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider()
    }
}