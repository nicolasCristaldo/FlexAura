package com.flexaura.features.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.flexaura.data.domain.model.Post

@Composable
fun HomeRoute(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is HomeUiEffect.NavigateToPostDetail -> onNavigateToPostDetail(effect.postId)
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
        HomeScreen(
            state = state.value,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onIntent: (HomeUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.newPostText,
            onValueChange = { onIntent(HomeUiIntent.OnNewPostTextChanged(it)) },
            label = { Text("Publish new Post") }
        )
        Button(
            onClick = { onIntent(HomeUiIntent.OnPublishPostClicked) },
            enabled = !state.isPostPublishing
        ) {
            if (state.isPostPublishing) CircularProgressIndicator()
            else Text("Publish")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.posts) { post ->
                PostCard(
                    post = post,
                    onVoteClicked = { postId, vote, currentVote ->
                        onIntent(HomeUiIntent.OnVoteClicked(postId, vote, currentVote))
                    },
                    onSavedClicked = { postId ->
                        onIntent(HomeUiIntent.OnSavedClicked(postId))
                    },
                    modifier = Modifier.clickable {
                        onIntent(HomeUiIntent.OnPostClicked(post.id))
                    }
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onVoteClicked: (postId: String, vote: Int, currentVote: Int) -> Unit,
    onSavedClicked: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
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